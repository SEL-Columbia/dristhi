require_relative 'lib/read_anc_services_from_xlsx.rb'
require_relative 'lib/read_ancs_from_xlsx.rb'
require_relative 'lib/read_ecs_from_xlsx.rb'
require_relative 'lib/forms.rb'
require_relative 'lib/mobile_workers.rb'

Dir['output/*.xml'].each { |file| FileUtils.rm_f file }
Dir['output/DONE/*.xml'].each { |file| FileUtils.rm_f file }

user_name = "test_upload"
#user_name = "latha"
#user_name = "elizab"
#user_name = "jyothi"
#user_name = "fathim"
#user_name = "hemala"
#user_name = "dhaksh"
#user_name = "kamala"
#user_name = "dhaksh1"
#user_name = "indira"
mobile_worker = MobileWorkers.new.find_by_user_name user_name
puts "Creating forms for user: '#{user_name}' with spreadsheet '#{mobile_worker.spreadsheet}'"

ecs = ECs.new(mobile_worker.spreadsheet).ecs
ancs_per_ec = ANCs.new(mobile_worker.spreadsheet).ancs_grouped_per_couple
anc_services_per_ec = ANCServices.new(mobile_worker.spreadsheet).anc_services_per_ec

puts "Got: ECs: #{ecs.size}. ANCS grouped by EC: #{ancs_per_ec.size}. Services grouped by EC: #{anc_services_per_ec.size}"

# In area ANCs.
ecs.each do |ec|
  key = [ec['Village Code'].village, ec['Wife Name'], ec['Husband Name']]

  Forms.new(mobile_worker, ec, ancs_per_ec[key], anc_services_per_ec[key]).fill_for_in_area
end

# Out of area ANCs.
ancs_per_ec.each do |anc_key, anc_values|
  next if ecs.any? { |ec| anc_key == [ec['Village Code'].village, ec['Wife Name'], ec['Husband Name']] }

  Forms.new(mobile_worker, nil, anc_values, anc_services_per_ec[anc_key]).fill_for_out_of_area
end