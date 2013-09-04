require 'roo'
require 'csv'
require 'erb'
require 'date'
require 'guid'
require './lib/care_import_row.rb'
require './lib/village.rb'

class ANCVisits
  def initialize xlsx_filename
    @anc_visits = []

    read_from xlsx_filename
  end
  
  def anc_visits
    @anc_visits.collect { |anc| anc.to_hash }
  end

  def anc_visits_grouped_per_couple
    anc_visits.group_by { |anc_visit| [anc_visit['Village Code'].village.downcase, anc_visit['Wife Name'].downcase, anc_visit['Husband Name'].downcase] }
  end
  

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_ANC_Visit_register.csv"
    sheet_name = "ANC Visit" 
    begin
      spreadsheet = Roo::Excelx.new xlsx_filename, nil, :ignore
      
      if spreadsheet.sheets.include? sheet_name
        spreadsheet.to_csv filename, sheet_name
      

        CSV.foreach(filename, {:headers => true}) do |csv_row|
          anc_visit = Row.new csv_row

          anc_visit.convert_value "Village Code", Village.code_to_village_hash
          anc_visit.convert_value "Wife Name", :empty => "Wife Name"
          anc_visit.convert_value "Husband Name", :empty => "Husband Name"
          anc_visit.convert_value "ANC visit number", :empty => "1"
          anc_visit.convert_value "ANC visit place", :empty => "phc"
          anc_visit.convert_value "ANC visit person", :empty => "other"
          anc_visit.convert_value "BP (systolic)", :empty => ""
          anc_visit.convert_value "BP (diastolic)", :empty => ""
          anc_visit.convert_value "Weight", :empty => ""
          anc_visit.convert_to_date "ANC visit date", :empty => Date.today.to_s


          anc_visit.add_field "Instance ID", Guid.new.to_s
          anc_visit.add_field "Entity ID", Guid.new.to_s
          anc_visit.add_field "Reference date", anc_visit['ANC visit date']

          puts "#{anc_visit['Wife Name']} - #{anc_visit['Husband Name']}"
          @anc_visits << anc_visit
        end
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end