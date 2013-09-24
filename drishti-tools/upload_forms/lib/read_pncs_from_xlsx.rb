require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class PNCs
  def initialize xlsx_filename
    @pncs = []

    read_from xlsx_filename
  end

  def pncs
    @pncs.collect { |pnc| pnc.to_hash }
  end

  def pncs_grouped_per_couple
    pncs.group_by { |pnc| [pnc['Village Code'].village.downcase, pnc['Wife Name'].downcase, pnc['Husband Name'].downcase] }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_PNC_register.csv"
    sheet_name = "PNC registration"
    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore

      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name


        CSV.foreach(filename, {:headers => true}) do |csv_row|
          pnc = Row.new csv_row

          pnc.convert_to_date "Registration date", :empty => Date.today.to_s
          pnc.convert_value "Wife Name", :empty => "Wife Name"
          pnc.convert_value "Husband Name", :empty => "Husband Name"
          pnc.convert_value "Village Code", Village.code_to_village_hash
          pnc.convert_value "OA", "yes" => "true", "no" => "false", :empty => "true"
          pnc.convert_value "Wife age", :empty => "20"
          pnc.convert_value "APL/BPL",
                            "BPL" => "bpl",
                            "APL" => "apl",
                            :empty => "apl",
                            :default => "apl"

          pnc.convert_value "Caste",
                            "SC" => "sc",
                            "ST" => "st",
                            :empty => "c_others",
                            :default => "c_others"

          pnc.convert_value "HR",
                            "No" => "no",
                            "Yes" => "yes",
                            :empty => "no",
                            :default => "no"

          pnc.convert_value "Thayi number", :empty => "1234567"
          pnc.convert_to_date "Delivery date", :empty => Date.today.to_s
          pnc.convert_value "Delivery place", :empty => "phc"
          pnc.convert_value "Delivery type", :empty => "normal"
          pnc.convert_value "Skilled delivery",
                            "No" => "no",
                            "Yes" => "yes",
                            :empty => "no",
                            :default => "no"

          pnc.convert_value "Delivery complications", :empty => ""
          pnc.convert_value "Delivery outcome", :empty => "still_birth"
          pnc.convert_value "Woman survived child birth",
                            "Yes" => "yes",
                            "No" => "no",
                            :empty => "yes",
                            :default => "yes"
          pnc.convert_value "Child HR",
                            "No" => "no",
                            "Yes" => "yes",
                            :empty => "no",
                            :default => "no"
          pnc.convert_value "Child sex",
                            "Male" => "male",
                            "Female" => "female",
                            :empty => "",
                            :default => ""

          pnc.convert_value "Child birthweight", :empty => ""
          pnc.convert_value "BF within 1 hr", :empty => "no"

          pnc.add_field "Instance ID", Guid.new.to_s
          pnc.add_field "Entity ID", Guid.new.to_s
          pnc.add_field "Child ID", Guid.new.to_s
          pnc.add_field "EC ID", Guid.new.to_s
          pnc.add_field "Reference date", pnc['Delivery date'] rescue Date.today.to_s
          pnc.add_field "Submission date", Date.today.to_s

          pnc['Delivery complications'] != "" ? pnc.add_field("Has Delivery Complications", "yes") :
              pnc.add_field("Has Delivery Complications", "no")
          @pncs << pnc
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
