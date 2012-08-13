require 'csv'
require 'date'
require 'erb'
require 'guid'
require './lib/care_import_row.rb'
require './lib/commcare.rb'

anc_registration_erb = ERB.new(File.read('templates/anc_registration.erb'))

CSV.foreach("examples/Munjanahalli_ANCs.csv", { :headers => true }) do |csv_row|
  row = Row.new csv_row

  row.default_value "House Number", "1"
  row.default_value "EC Number", "1"
  row.default_value "Thayi Card Number", "1"
  row.default_value "Wife Name", "Prerna"
  row.default_value "Wife Age", "32"
  row.default_value "Husband Name", "Dooresh"
  row.default_value "LMP", Date.today.to_s
  row.default_value "Gravida", "0"
  row.default_value "Number of abortion(A)", "0"
  row.default_value "Number of livebirth(L)", "0"
  row.default_value "Place of Delivery", "0"
  row.default_value "EDD", Date.today.to_s

  row.add_field "Case ID", Guid.new.to_s
  row.add_field "Instance ID", Guid.new.to_s

  registration_xml = anc_registration_erb.result(binding)

  puts "Uploading ANC: #{row.field('Thayi Card Number')} - EC: #{row.field('EC Number')} - #{row.field('Wife Name')} and #{row.field('Husband Name')}"
  CommCare.new.upload registration_xml
  sleep 3
end
