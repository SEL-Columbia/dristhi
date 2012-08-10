require 'csv'
require 'erb'
require 'guid'
require './lib/care_import_row.rb'
require './lib/commcare.rb'

ec_registration_erb = ERB.new(File.read('templates/ec_registration.erb'))
ec_family_planning_erb = ERB.new(File.read('templates/ec_family_planning.erb'))

CSV.foreach("Munjanahalli_merges.csv", { :headers => true }) do |csv_row|
  row = Row.new csv_row

  row.default_value "Registration date", Date.today.to_s
  row.default_value "Village Code", "29230030060"
  row.default_value "Village Name", "MUNJANAHALLI"
  row.default_value "House Number", "0"
  row.default_value "EC Number ", "0"
  row.default_value "Wife Name", "Jyothi"
  row.default_value "Wife Age", "31"
  row.default_value "Husband Name", "Gopala"
  row.default_value "Number of Abortion", "0"
  row.default_value "Number of Still Birth", "0"
  row.default_value "Number of Living Children", "0"
  row.default_value "DOB of youngest child ", Date.today.to_s
  row.default_value "Status of EC Woman [Permanent/ Continuting/Discontinued]", "Permanent Method"
  row.default_value "FP Method", "none"
  row.default_value "Acceptance Date", Date.today.to_s
  row.default_value "Pregnancy [Yes/No]", "N"
  row.default_value "if Yes LMP date", Date.today.to_s
  row.default_value "Thayi Card Number", "THAAYI 1"
  row.default_value "Head of Family", "Ramanayaka"
  row.default_value "Number of Members in Family", "2"
  row.default_value "Number of Eligible Couples", "1"
  row.default_value "Religion (Hindu/ Muslim/ Christian/Others)", "Hindu"
  row.default_value "Caste ( SC / ST / Others)", "Others"
  row.default_value "BPL (Yes/ No)", "No"
  row.default_value "Water Tank (Yes/No)", "No"
  row.default_value "Toilet (Yes/No)", "No"
  row.default_value "Male Children   (0-1)Yrs", "0"
  row.default_value "Female children (0-1)Yrs", "0"
  row.default_value "Male children(1-5)Yrs", "0"
  row.default_value "Female children(1-5)Yrs", "0"
  row.default_value "Male  children (5-10)Yrs", "0"
  row.default_value "Female children (5-10)Yrs", "0"
  row.default_value "Male children   (10-18)Yrs", "0"
  row.default_value "Female children    (10-18)Yrs", "0"

  row.add_field "Case ID", Guid.new.to_s

  row.add_field "Instance ID", Guid.new.to_s
  registration_xml = ec_registration_erb.result(binding)

  puts "Uploading EC: #{row.field('EC Number ')} - #{row.field('Wife Name')} and #{row.field('Husband Name')}"
  CommCare.new.upload registration_xml
  sleep 3
end

