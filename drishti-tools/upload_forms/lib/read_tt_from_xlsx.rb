require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class TTs
  def initialize xlsx_filename
    @tts = []

    read_from xlsx_filename
  end

  def tts
    @tts.collect { |ifa| ifa.to_hash }
  end

  def tt_grouped_per_couple
    tts.group_by { |tt| [tt['Village Code'].village.downcase, tt['Wife Name'].downcase, tt['Husband Name'].downcase] }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_TT.csv"
    sheet_name = "TT"
    begin
      spreadsheet = Roo::Excelx.new xlsx_filename, nil, :ignore
      
      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name
      
        CSV.foreach(filename, {:headers => true}) do |csv_row|
          tt = Row.new csv_row

          tt.convert_value "Village Code", Village.code_to_village_hash
          tt.convert_value "Wife Name", :empty => "Wife Name"
          tt.convert_value "Husband Name", :empty => "Husband Name"
          tt.convert_to_date "TT date", :empty => Date.today.to_s
          tt.convert_value "TT dose", :empty => "1"
          tt.convert_value "TT Injection place", :empty => "phc"

          tt.add_field "Instance ID", Guid.new.to_s
          tt.add_field "Entity ID", Guid.new.to_s
          tt.add_field "Submission date", Date.today.to_s

          puts "#{tt['Wife Name']} - #{tt['Husband Name']}"
          @tts << tt
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
