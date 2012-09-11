require 'csv'
require 'erb'
require 'guid'
require './lib/care_import_row.rb'
require './lib/commcare.rb'

ec_registration_erb = ERB.new(File.read('templates/ec_registration.erb'))
anc_registration_erb = ERB.new(File.read('templates/anc_registration.erb'))

CSV.foreach("examples/U_Munjanhalli_Village_Kavalahosur_ANC_EC_Merged.csv", { :headers => true }) do |csv_row|
  row = Row.new csv_row

  row.default_value "ANC_Acceptance Date", Date.today.to_s
  row.default_value "ANC_Address", "Address"
  row.default_value "ANC_BPL", "no"
  row.default_value "ANC_Caste", "Others"
  row.default_value "ANC_Child Weight at Delivery time", "3"
  row.default_value "ANC_DOB of youngest child", Date.today.to_s
  row.default_value "ANC_Date of Delivery", Date.today.to_s
  row.default_value "ANC_Date of Left the Place", Date.today.to_s
  row.default_value "ANC_Duration of Pregnancy in Weeks", "40"
  row.default_value "ANC_EC Not avoilable", ""
  row.default_value "ANC_EC Number", "111111"
  row.default_value "ANC_EDD", Date.today.to_s
  row.default_value "ANC_Education", ""
  row.default_value "ANC_FP Method", "Sterilization"
  row.default_value "ANC_Gravida", ""
  row.default_value "ANC_HRP", "No"
  row.default_value "ANC_Height", "150"
  row.default_value "ANC_Husband Age", "20"
  row.default_value "ANC_LMP", Date.today.to_s
  row.default_value "ANC_New EC No", "111111"
  row.default_value "ANC_No of ANM Visits", "0"
  row.default_value "ANC_No of Living Female Children", "0"
  row.default_value "ANC_No of Living Male Children", "0"
  row.default_value "ANC_Number of Abortion", "0"
  row.default_value "ANC_Number of Living Children", "0"
  row.default_value "ANC_Number of Still Birth", "0"
  row.default_value "ANC_Number of abortion(A)", "0"
  row.default_value "ANC_Number of livebirth(L)", "0"
  row.default_value "ANC_Number of parity(P)", "0"
  row.default_value "ANC_Occupation", ""
  row.default_value "ANC_Old  EC No", "0"
  row.default_value "ANC_Out of area", ""
  row.default_value "ANC_Outcomes", ""
  row.default_value "ANC_Place of Delivery", "Unknown"
  row.default_value "ANC_Religion", "Others"
  row.default_value "ANC_Sno", "111111"
  row.default_value "ANC_Year", "2012"
  row.default_value "ANC_a.House Number", "1"
  row.default_value "ANC_a.Husband Name", "Husband"
  row.default_value "ANC_a.Registration date", Date.today.to_s
  row.default_value "ANC_a.Thayi Card Number", "1234567"
  row.default_value "ANC_a.Village Code", "12121212"
  row.default_value "ANC_a.Village Name", "Munjanahalli"
  row.default_value "ANC_a.Wife Age", ""
  row.default_value "ANC_a.Wife Name", ""
  row.default_value "ANC_e.House Number", ""
  row.default_value "ANC_e.Husband Name", ""
  row.default_value "ANC_e.Registration date", ""
  row.default_value "ANC_e.Thayi Card Number", ""
  row.default_value "ANC_e.Village Code", ""
  row.default_value "ANC_e.Village Name", ""
  row.default_value "ANC_e.Wife Age", ""
  row.default_value "ANC_e.Wife Name", ""
  row.default_value "ANC_if Yes LMP date", ""
  row.default_value "EC_Acceptance Date", ""
  row.default_value "EC_DOB of youngest child ", ""
  row.default_value "EC_EC Number ", ""
  row.default_value "EC_FP Method", ""
  row.default_value "EC_House Number ", ""
  row.default_value "EC_Husband Name", ""
  row.default_value "EC_NID", ""
  row.default_value "EC_Number of Abortion", ""
  row.default_value "EC_Number of Living Children", ""
  row.default_value "EC_Number of Still Birth", ""
  row.default_value "EC_Pregnancy [Yes/No]", ""
  row.default_value "EC_Registration date", ""
  row.default_value "EC_Status of EC Woman [Permanent/ Continuting/Discontinued]", ""
  row.default_value "EC_Thayi Card Number", ""
  row.default_value "EC_Village Code", "29230030060"
  row.default_value "EC_Village Name", "munjanahalli"
  row.default_value "EC_Wife Age", "20"
  row.default_value "EC_Wife Name", "Unknown"
  row.default_value "EC_if Yes LMP date", ""

  row.add_field "Head of Family", "Head"

  row.add_field "EC_Case ID", Guid.new.to_s
  row.add_field "EC_Instance ID", Guid.new.to_s

  row.add_field "ANC_Case ID", Guid.new.to_s
  row.add_field "ANC_Instance ID", Guid.new.to_s

  ec_registration_xml = ec_registration_erb.result(binding)
  anc_registration_xml = anc_registration_erb.result(binding)

  # File.open("output/EC_#{row.field('EC_Village Name')}_#{row.field('EC_EC Number ')}.xml", "w") do |f| f.puts ec_registration_xml end
  # File.open("output/ANC_#{row.field('EC_Village Name')}_#{row.field('EC_EC Number ')}.xml", "w") do |f| f.puts anc_registration_xml end

  puts "Uploading EC: #{row.field('EC_EC Number ')} - #{row.field('EC_Wife Name')} and #{row.field('EC_Husband Name')}"

  CommCare.new.upload ec_registration_xml
  CommCare.new.upload anc_registration_xml
  sleep 3
end

