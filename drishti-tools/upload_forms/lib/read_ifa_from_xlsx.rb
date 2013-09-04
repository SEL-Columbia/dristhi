require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class IFAs
  def initialize xlsx_filename
    @ifas = []

    read_from xlsx_filename
  end

  def ifas
    @ifas.collect { |ifa| ifa.to_hash }
  end

  def ifa_grouped_per_couple
    ifas.group_by { |ifa| [ifa['Village Code'].village.downcase, ifa['Wife Name'].downcase, ifa['Husband Name'].downcase] }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_IFA.csv"
    sheet_name = "IFA"
    begin
      spreadsheet = Roo::Excelx.new xlsx_filename, nil, :ignore
      
      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name
      
        CSV.foreach(filename, {:headers => true}) do |csv_row|
          ifa = Row.new csv_row

          ifa.convert_value "Village Code", Village.code_to_village_hash
          ifa.convert_value "Wife Name", :empty => "Wife Name"
          ifa.convert_value "Husband Name", :empty => "Husband Name"
          ifa.convert_to_date "IFA date", :empty => Date.today.to_s
          ifa.convert_value "IFA tablets", :empty => "0"

          ifa.add_field "Instance ID", Guid.new.to_s
          ifa.add_field "Entity ID", Guid.new.to_s
          ifa.add_field "Submission date", Date.today.to_s

          puts "#{ifa['Wife Name']} - #{ifa['Husband Name']}"
          @ifas << ifa
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
