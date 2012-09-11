require 'csv'
require 'erb'
require 'guid'
require './lib/care_import_row.rb'
require './lib/commcare.rb'

out_of_area_anc_registration = ERB.new(File.read('templates/out_of_area_anc_registration.erb'))

CSV.foreach("examples/U_Munjanhalli_SC_ANC_Out_of_Area.csv", { :headers => true }) do |csv_row|
  row = Row.new csv_row

  row.default_value "Year", "2012"
  row.default_value "a.Village Code", "29230030060"
  row.default_value "a.Village Name", "munjanahalli"
  row.default_value "a.Wife Name", "Wife Name"
  row.default_value "a.Wife Age", "20"
  row.default_value "a.Husband Name", "Husband Name"
  row.default_value "Sno", "O/A 111111"
  row.default_value "a.Registration date", Date.today.to_s
  row.default_value "a.House Number", "111111"
  row.default_value "New EC No", "111111"
  row.default_value "Old  EC No", "111111"
  row.default_value "a.Thayi Card Number", "1234567"
  row.default_value "Husband Age", "20"
  row.default_value "Address", "Address unknown"
  row.default_value "HRP", "No"
  row.default_value "Religion", "Other"
  row.default_value "Caste", "Others"
  row.default_value "Education", "Unknown"
  row.default_value "Occupation", "Unknown"
  row.default_value "BPL", "No"
  row.default_value "No of Living Male Children", "0"
  row.default_value "No of Living Female Children", "0"
  row.default_value "Gravida", "0"
  row.default_value "Number of parity(P)", "0"
  row.default_value "Number of livebirth(L)", "0"
  row.default_value "Number of abortion(A)", "0"
  row.default_value "Duration of Pregnancy in Weeks", "36"
  row.default_value "LMP", Date.today.to_s
  row.default_value "EDD", Date.today.to_s
  row.default_value "Height", "150"
  row.default_value "Out of area", "Yes"
  row.default_value "No of ANM Visits", "0"
  row.default_value "Date of Delivery", Date.today.to_s
  row.default_value "Outcomes", "Unknown"
  row.default_value "Child Weight at Delivery time", "2.5"
  row.default_value "Place of Delivery", "Unknown"
  row.default_value "Date of Left the Place", Date.today.to_s

  row.add_field "Case ID", Guid.new.to_s

  row.add_field "Instance ID", Guid.new.to_s
  registration_xml = out_of_area_anc_registration.result(binding)
  # File.open("output/OOANC_#{row.field('a.Village Code')}_#{row.field('a.Thayi Card Number')}.xml", "w") do |f| f.puts registration_xml end

  puts "Uploading ANC: #{row.field('Sno')} - #{row.field('a.Wife Name')} and #{row.field('a.Husband Name')}"
  CommCare.new.upload registration_xml
  sleep 2
end

