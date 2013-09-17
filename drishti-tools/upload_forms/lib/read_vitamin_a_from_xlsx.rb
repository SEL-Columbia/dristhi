require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'


class VitaminADosages
  def initialize xlsx_filename
    @vitamin_a_dosages = []

    read_from xlsx_filename
  end

  def vitamin_a_dosages
    @vitamin_a_dosages.collect { |vitamin_a_dosage| vitamin_a_dosage.to_hash }
  end

  def vitamin_a_dosages_grouped_per_couple
    vitamin_a_dosages.group_by { |vitamin_a_dosage| [vitamin_a_dosage['Village Code'].village.downcase, vitamin_a_dosage['Wife Name'].downcase, vitamin_a_dosage['Husband Name'].downcase] }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_Vitamin_A.csv"
    sheet_name = "Vitamin A"
    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore
      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name


        CSV.foreach(filename, {:headers => true}) do |csv_row|
          vitamin_a = Row.new csv_row

          vitamin_a.convert_value "Wife Name", :empty => "Wife Name"
          vitamin_a.convert_value "Husband Name", :empty => "Husband Name"
          vitamin_a.convert_value "Village Code", Village.code_to_village_hash
          vitamin_a.convert_value "Vitamin A dose", :empty => ""
          vitamin_a['Vitamin A dose'].downcase!
          vitamin_a.convert_to_date "Vitamin A date", :empty => Date.today.to_s

          vitamin_a.add_field "Submission date", Date.today.to_s
          vitamin_a.add_field "Instance ID", Guid.new.to_s
          vitamin_a.add_field "Vitamin A Dosage Place", "sub_center"

          @vitamin_a_dosages << vitamin_a
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
