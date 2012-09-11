require 'csv'
require 'erb'
require 'guid'
require './lib/care_import_row.rb'
require './lib/commcare.rb'

ec_registration_without_anc_erb = ERB.new(File.read('templates/ec_registration_without_anc.erb'))

CSV.foreach("examples/U_Munjanhalli_ECs_Without_ANC.csv", { :headers => true }) do |csv_row|
  row = Row.new csv_row

  row.default_value "Registration date", Date.today.to_s
  row.default_value "Village Code", "29230030060"
  row.default_value "Village Name", "munjanahalli"
  row.default_value "House Number ", "111111"
  row.default_value "EC Number ", "111111"
  row.default_value "Wife Name", "Wife Unknown"
  row.default_value "Wife Age", "20"
  row.default_value "Husband Name", "Husband Unknown"
  row.default_value "Number of Abortion", "0"
  row.default_value "Number of Still Birth", "0"
  row.default_value "Number of Living Children", "0"
  row.default_value "DOB of youngest child ", Date.today.to_s
  row.default_value "Status of EC Woman [Permanent/ Continuting/Discontinued]", "Continuting"
  row.default_value "FP Method", "None"
  row.default_value "Acceptance Date", Date.today.to_s
  row.default_value "Pregnancy [Yes/No]", "No"
  row.default_value "if Yes LMP date", ""
  row.default_value "Thayi Card Number", "1234567"

  row.add_field "Case ID", Guid.new.to_s

  row.add_field "Instance ID", Guid.new.to_s
  registration_xml = ec_registration_without_anc_erb.result(binding)
  # File.open("output/EC_#{row.field('Village Name')}_#{row.field('EC Number ')}.xml", "w") do |f| f.puts registration_xml end

  puts "Uploading EC: #{row.field('EC Number ')} - #{row.field('Wife Name')} and #{row.field('Husband Name')}"
  CommCare.new.upload registration_xml
  sleep 2
end

