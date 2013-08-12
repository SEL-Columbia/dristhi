require_relative 'lib/read_ecs_from_xlsx.rb'
require_relative 'lib/forms.rb'
require_relative 'lib/mobile_workers.rb'

Dir['output/*.xml'].each { |file| FileUtils.rm_f file }
Dir['output/DONE/*.xml'].each { |file| FileUtils.rm_f file }

user_name = ARGV[0].to_s

mobile_worker = MobileWorkers.new.find_by_user_name user_name
puts "Creating forms for user: '#{user_name}' with spreadsheet '#{mobile_worker.spreadsheet}'"

ecs = ECs.new(mobile_worker.spreadsheet).ecs
#ancs_per_ec = ANCs.new(mobile_worker.spreadsheet).ancs_grouped_per_couple
#anc_services_per_ec = ANCServices.new(mobile_worker.spreadsheet).anc_services_per_ec

puts "Got: ECs: #{ecs.size}"

ecs.each do |ec|
  Forms.new(mobile_worker, ec, [], []).fill_ec_form
end
