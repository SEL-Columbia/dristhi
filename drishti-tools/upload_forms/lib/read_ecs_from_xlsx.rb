require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class ECs
  def initialize xlsx_filename
    @ecs = []

    read_from xlsx_filename
  end

  def ecs
    @ecs.collect { |ec| ec.to_hash }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_EC_register.csv"

    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore
      spreadsheet.to_csv filename, "EC register"

      CSV.foreach(filename, {:headers => true}) do |csv_row|
        ec = Row.new csv_row

        ec.convert_to_date "Registration date", :empty => Date.today.to_s
        registration_date = Date.parse(ec['Registration date'])
        ec.convert_value "House Number", :empty => ""
        ec.convert_value "EC Number", :empty => "111111"
        ec.convert_value "Wife Name", :empty => "Wife Unknown"
        ec.convert_value "Wife Age", :empty => "20"
        ec.convert_value "Wife DOB", :empty => (registration_date.year - ec['Wife Age'].to_i).to_s + "-" + registration_date.month.to_s + "-" + registration_date.day.to_s
        ec.convert_value "Husband Name", :empty => "Husband Unknown"
        ec.convert_value "Husband Age", :empty => "25"
        ec.convert_value "Husband DOB", :empty => (registration_date.year - ec['Husband Age'].to_i).to_s + "-" + registration_date.month.to_s + "-" + registration_date.day.to_s
        ec.convert_value "Religion", :empty => ""
        ec.convert_value "Caste",
                         "ST" => "st",
                         "SC" => "sc",
                         "Others" => "c_others",
                         :empty => ""
        ec.convert_value "APL/BPL",
                         "APL" => "apl",
                         "BPL" => "bpl",
                         :empty => ""
        ec.convert_value "HP", :empty => ""
        ec.convert_value "Risks", :empty => ""
        ec.convert_value "Number of Pregnancies", :empty => "0"
        ec.convert_value "Number of Abortion", :empty => "0"
        ec.convert_value "Number of Still Birth", :empty => "0"
        ec.convert_value "Number of Live Birth", :empty => "0"
        ec.convert_value "Number of Living Children", :empty => "0"
        ec.convert_value "Male", :empty => "0"
        ec.convert_value "Female", :empty => "0"
        ec.convert_value "Age of youngest child (in months)", :empty => "0"
        ec.convert_to_date "Youngest child DOB", :empty => (Date.today << ec['Age of youngest child (in months)'].to_i).to_s
        ec.add_field "is youngest child under two", ec['Age of youngest child (in months)'].to_i < 24 ? "yes" : "no"

        ec.convert_value "Village Code", Village.code_to_village_hash
        ec.add_field "Parity", (ec['Number of Still Birth'].to_i + ec['Number of Live Birth'].to_i).to_s
        ec.convert_value "FP Method",
                         "OCP" => "ocp",
                         "IUD" => "iud",
                         "Condom" => "condom",
                         "DMPA/Injectable" => "dmpa_injectable",
                         "Male Sterilization" => "male_sterilization",
                         "LAM" => "lam",
                         "Traditional Methods" => "traditional_methods",
                         "Female Sterilization" => "female_sterilization",
                         :empty => "none",
                         :default => "none"

        ec.convert_to_date "Acceptance Date", :empty => Date.today.to_s
        ec.add_field "DMPA Injection Date", (ec['FP Method'] == 'dmpa_injectable' ? ec['Acceptance Date'] : nil)
        ec.add_field "FP Start Date", ec['FP Method'] != 'none' ? ((Date.parse(ec['Acceptance Date']).to_s rescue Date.today.to_s)) : ''
        ec.add_field "Entity ID", Guid.new.to_s
        ec.add_field "Instance ID", Guid.new.to_s

        @ecs << ec
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
