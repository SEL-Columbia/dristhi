require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class Children
  def initialize xlsx_filename
    @children = []

    read_from xlsx_filename
  end

  def children
    @children.collect { |child| child.to_hash }
  end

  def children_grouped_per_couple
    children.group_by { |child| [child['Village Code'].village.downcase, child['Wife Name'].downcase, child['Husband Name'].downcase] }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_Child_register.csv"
    sheet_name = "Child register"
    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore
      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name


        CSV.foreach(filename, {:headers => true}) do |csv_row|
          child = Row.new csv_row

          child.convert_value "Wife Name", :empty => "Wife Name"
          child.convert_value "Husband Name", :empty => "Husband Name"
          child.convert_value "Village Code", Village.code_to_village_hash
          child.convert_value "Child HR",
                              "No" => "no",
                              "Yes" => "yes",
                              :empty => "no",
                              :default => "no"
          child.convert_value "Child sex", :empty => "male"
          child.convert_value "Child name", :empty => ("B/o " + child['Wife Name'].to_s.capitalize)
          child.convert_to_date "Birth date", :empty => Date.today.to_s
          child.convert_value "Thayi number", :empty => "1234567"

          child.add_field "Entity ID", Guid.new.to_s
          child.add_field "Submission date", Date.today.to_s
          child.add_field "Instance ID", Guid.new.to_s

          @children << child
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
