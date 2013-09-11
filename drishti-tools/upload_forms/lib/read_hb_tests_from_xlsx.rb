require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class HbTests
  def initialize xlsx_filename
    @hb_tests = []

    read_from xlsx_filename
  end

  def hb_tests
    @hb_tests.collect { |hb_test| hb_test.to_hash }
  end

  def hb_tests_grouped_per_couple
    hb_tests.group_by { |hb_test| [hb_test['Village Code'].village.downcase, hb_test['Wife Name'].downcase, hb_test['Husband Name'].downcase] }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_Hb_Test.csv"
    sheet_name = "Hb Test"
    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore
      
      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name
      
        CSV.foreach(filename, {:headers => true}) do |csv_row|
          hb_test = Row.new csv_row

          hb_test.convert_value "Village Code", Village.code_to_village_hash
          hb_test.convert_value "Wife Name", :empty => "Wife Name"
          hb_test.convert_value "Husband Name", :empty => "Husband Name"
          hb_test.convert_to_date "Hb test date", :empty => Date.today.to_s
          hb_test.convert_value "Hb test place", :empty => "phc"
          hb_test.convert_value "Hb level", :empty => "11"
          hb_test.convert_value "HRP",
                            "No" => "no",
                            "Yes" => "yes",
                            :empty => "no",
                            :default => "no"


          hb_test.add_field "Instance ID", Guid.new.to_s
          hb_test.add_field "Entity ID", Guid.new.to_s
          hb_test.add_field "Submission date", Date.today.to_s

          @hb_tests << hb_test
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
