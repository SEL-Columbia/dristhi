require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class PPFPs
  def initialize xlsx_filename
    @ppfp_list = []

    read_from xlsx_filename
  end

  def ppfpList
    @ppfp_list.collect { |ppfp| ppfp.to_hash }
  end

  def ppfps_grouped_per_couple
    ppfpList.group_by { |ppfp| [ppfp['Village Code'].village.downcase, ppfp['Wife Name'].downcase, ppfp['Husband Name'].downcase] }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_PPFP_register.csv"
    sheet_name = "PP FP"
    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore

      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name


        CSV.foreach(filename, {:headers => true}) do |csv_row|
          ppfp = Row.new csv_row

          ppfp.convert_value "Wife Name", :empty => "Wife Name"
          ppfp.convert_value "Husband Name", :empty => "Husband Name"
          ppfp.convert_value "Village Code", Village.code_to_village_hash
          ppfp.convert_value "PP FP Method",
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

          ppfp.convert_to_date "PP FP Method start date", :empty => Date.today.to_s
          ppfp.convert_value "IUD place", :empty => ""
          ppfp.convert_value "IUD person", :empty => ""
          ppfp.convert_value "OCP strips", :empty => ""
          ppfp.convert_value "Condoms", :empty => ""

          ppfp.add_field "Instance ID", Guid.new.to_s
          ppfp.add_field "Submission date", Date.today.to_s

          @ppfp_list << ppfp
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
