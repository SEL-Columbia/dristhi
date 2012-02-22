require 'csv'
require 'erb'
require './lib/care_random_id.rb'
require './lib/care_import_row.rb'
require './lib/commcare.rb'

random = RandomCommCareID.new
ec_registration_erb = ERB.new(File.read('templates/ec_registration.erb'))
ec_family_planning_erb = ERB.new(File.read('templates/ec_family_planning.erb'))

CSV.foreach("eligible Couples.csv", { :headers => true }) do |csv_row|
  row = Row.new csv_row

  row.default_value "Village Code", "102"
  row.default_value "HH Number", "999"
  row.default_value "Head of House Hold", "HEAD"
  row.default_value "EC Number", "999"
  row.default_value "Wife Name", "WIFE_NAME"
  row.default_value "Wife's Age", "23"
  row.default_value "Husband Name", "HUSBAND_NAME"
  row.default_value "Religion", "jain"
  row.default_value "Caste", "other"
  row.default_value "EC status", "apl"
  row.default_value "Gravida ( number of Pregnancies)", "1"
  row.default_value "Number of Abortion", "0"
  row.default_value "Number of Still Birth", "0"
  row.default_value "Number of Living Children", "1"
  row.default_value "Age of youngest Child", "1"
  row.default_value "Status of EC Woman", "on_family_planning"
  row.default_value "FP Method", "iud"
  row.default_value "FP Start Date", Date.today.to_s
  row.default_value "Thayi Card Number", random.next(4, :digits)
  row.default_value "Case ID", random.next(25, :alphanumeric)

  row.default_value "Instance ID", random.next(25, :alphanumeric)
  registration_xml = ec_registration_erb.result(binding)

  row.default_value "Instance ID", random.next(25, :alphanumeric)
  family_planning_xml = ec_family_planning_erb.result(binding)

  puts "Uploading #{row.field('Wife Name')} and #{row.field('Husband Name')}"
  CommCare.new.upload registration_xml
  CommCare.new.upload family_planning_xml
end

