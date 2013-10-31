require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class PNCVisits
  def initialize xlsx_filename
    @pnc_visits = []

    read_from xlsx_filename
  end
  
  def pnc_visits
    @pnc_visits.collect { |pnc| pnc.to_hash }
  end

  def pnc_visits_grouped_per_couple
    pnc_visits.group_by { |pnc_visit| [pnc_visit['Village Code'].village.downcase, pnc_visit['Wife Name'].downcase, pnc_visit['Husband Name'].downcase] }
  end
  

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_PNC_Visit_register.csv"
    sheet_name = "PNC Visit"
    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore
      
      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name
      

        CSV.foreach(filename, {:headers => true}) do |csv_row|
          pnc_visit = Row.new csv_row

          pnc_visit.convert_value "Wife Name", :empty => "Wife Name"
          pnc_visit.convert_value "Husband Name", :empty => "Husband Name"
          pnc_visit.convert_value "Village Code", Village.code_to_village_hash
          pnc_visit.convert_to_date "Discharge date", :empty => Date.today.to_s
          pnc_visit.convert_to_date "Visit date", :empty => Date.today.to_s
          pnc_visit.convert_value "Visit place", :empty => "phc"
          pnc_visit.convert_value "Visit person", :empty => "anm"

          pnc_visit.add_field "Instance ID", Guid.new.to_s
          pnc_visit.add_field "Submission date", Date.today.to_s

          @pnc_visits << pnc_visit
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end