require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'


class ChildImmunizations
  def initialize xlsx_filename
    @child_immunizations = []

    read_from xlsx_filename
  end

  def child_immunizations
    @child_immunizations.collect { |child_immunization| child_immunization.to_hash }
  end

  def child_immunization_grouped_per_couple
    child_immunizations.group_by { |child_immunization| [child_immunization['Village Code'].village.downcase, child_immunization['Wife Name'].downcase, child_immunization['Husband Name'].downcase] }
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_Child_immunizations.csv"
    sheet_name = "Child Immunizations"
    begin
      spreadsheet = Roo::Excelx.new xlsx_filename, nil, :ignore
      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name


        CSV.foreach(filename, {:headers => true}) do |csv_row|
          child_immunization = Row.new csv_row

          child_immunization.convert_value "Wife Name", :empty => "Wife Name"
          child_immunization.convert_value "Husband Name", :empty => "Husband Name"
          child_immunization.convert_value "Village Code", Village.code_to_village_hash

          child_immunization.convert_value "Immunization", :empty => ""
          child_immunization.convert_to_date "Immunization date", :empty => Date.today.to_s

          child_immunization.add_field "Submission date", Date.today.to_s
          child_immunization.add_field "Immunization Place", "sub_center"
          child_immunization.add_field "Instance ID", Guid.new.to_s

          child_immunization['Immunization'].downcase!

          @child_immunizations << child_immunization
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
