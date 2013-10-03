require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class ANCs
  def initialize xlsx_filename
    @ancs = []

    read_from xlsx_filename
  end

  def ancs
    @ancs.collect { |anc| anc.to_hash }
  end

  def ancs_grouped_per_couple
    ancs.group_by { |anc| [anc['Village Code'].village.downcase, anc['Wife Name'].downcase, anc['Husband Name'].downcase] }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_ANC_register.csv"
    sheet_name = "ANC Registration" 
    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore
      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name


        CSV.foreach(filename, {:headers => true}) do |csv_row|
          anc = Row.new csv_row

          anc.convert_value "Village Code", Village.code_to_village_hash
          anc.convert_value "Wife Name", :empty => "Wife Name"
          anc.convert_value "Wife Age", :empty => "20"
          anc.convert_value "Husband Name", :empty => "Husband Name"
          anc.convert_value "Registration place", :empty => ""
          anc.convert_value "ANC number", :empty => "111111"
          anc.convert_value "Thayi number", :empty => "1234567"
          anc.convert_to_date "Registration date", :empty => Date.today.to_s
          anc.convert_to_date "LMP", :empty => Date.today.to_s
          anc.convert_value "HRP reasons", :empty => ""
          anc.convert_value "OA",
                            "no" => "false",
                            "yes" => "true",
                            :empty => "true"
          anc.convert_value "HRP",
                            "No" => "no",
                            "Yes" => "yes",
                            :empty => "no",
                            :default => "no"

          anc.convert_value "Caste",
                            "SC" => "sc",
                            "ST" => "st",
                            :empty => "c_others",
                            :default => "c_others"

          anc.convert_value "APL/BPL",
                            "BPL" => "bpl",
                            "APL" => "apl",
                            :empty => "apl",
                            :default => "apl"
          anc.convert_value "JSY", :empty => "no"
          anc.convert_value "First pregnancy", :empty => "no"

          anc.add_field "Instance ID", Guid.new.to_s
          anc.add_field "Entity ID", Guid.new.to_s
          anc.add_field "EC ID", Guid.new.to_s #this should be used only for OA ANCs
          anc.add_field "Reference date", anc['LMP']
          anc.add_field "Year", :empty => Date.today.to_s, :default => Date.today.year.to_s
          anc.add_field "EDD", (Date.parse(anc['LMP']) + 280).strftime('%a, %d %b %Y %T GMT')

          @ancs << anc
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
