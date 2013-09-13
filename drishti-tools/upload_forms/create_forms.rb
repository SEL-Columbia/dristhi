require_relative 'lib/read_ecs_from_xlsx.rb'
require_relative 'lib/read_ancs_from_xlsx.rb'
require_relative 'lib/read_anc_visits_from_xlsx.rb'
require_relative 'lib/read_hb_tests_from_xlsx.rb'
require_relative 'lib/read_ifa_from_xlsx.rb'
require_relative 'lib/read_tt_from_xlsx.rb'
require_relative 'lib/read_pncs_from_xlsx.rb'
require_relative 'lib/read_pnc_visits_from_xlsx.rb'
require_relative 'lib/read_ppfp_from_xlsx.rb'
require_relative 'lib/read_children_from_xlsx.rb'

require_relative 'lib/forms.rb'
require_relative 'lib/mobile_workers.rb'

Dir['output/*.xml'].each { |file| FileUtils.rm_f file }
Dir['output/DONE/*.xml'].each { |file| FileUtils.rm_f file }

user_name = ARGV[0].to_s

mobile_worker = MobileWorkers.new.find_by_user_name user_name
puts "Creating forms for user: '#{user_name}' with spreadsheet '#{mobile_worker.spreadsheet}'"

ecs = ECs.new(mobile_worker.spreadsheet).ecs
ancs_per_ec = ANCs.new(mobile_worker.spreadsheet).ancs_grouped_per_couple
ancs_in_area = ancs_per_ec.reject { |k, v| v[0]['OA'].downcase == 'true' }
anc_visits = ANCVisits.new(mobile_worker.spreadsheet).anc_visits_grouped_per_couple
hb_tests = HbTests.new(mobile_worker.spreadsheet).hb_tests_grouped_per_couple
ifas = IFAs.new(mobile_worker.spreadsheet).ifa_grouped_per_couple
tts = TTs.new(mobile_worker.spreadsheet).tt_grouped_per_couple
pncs = PNCs.new(mobile_worker.spreadsheet).pncs_grouped_per_couple
pncs_out_of_area = pncs.reject { |k, v| v[0]['OA'].downcase != 'true' }
pncs_in_area = pncs.reject { |k, v| v[0]['OA'].downcase == 'true' }
pnc_visits = PNCVisits.new(mobile_worker.spreadsheet).pnc_visits_grouped_per_couple
ppfp_list = PPFPs.new(mobile_worker.spreadsheet).ppfps_grouped_per_couple
children = Children.new(mobile_worker.spreadsheet).children_grouped_per_couple

puts "Got: ECs: #{ecs.size}"

ecs.each do |ec|
  anc_for_ec = ancs_in_area.select { |k, v|
    k == [ec['Village Code'].village.downcase, ec['Wife Name'].downcase, ec['Husband Name'].downcase]
  }
  Forms.new(mobile_worker, ec, anc_for_ec.values, [], [], [], [], [], [], [], []).fill_for_in_area
end

ancs_per_ec.each do |anc_key, anc_values|
  if anc_values[0]['OA'].downcase == "true" then
    Forms.new(mobile_worker, nil, anc_values, [], [], [], [], [], [], [], []).fill_for_out_of_area
  end
end

anc_visits.each do |visit_key, visit_value|
  Forms.new(mobile_worker, ecs, ancs_per_ec, visit_value, [], [], [], [], [], [], []).fill_anc_visits_forms
end

hb_tests.each do |key, value|
  Forms.new(mobile_worker, ecs, ancs_per_ec, [], value, [], [], [], [], [],[]).fill_hb_tests_forms
end

ifas.each do |key, value|
  Forms.new(mobile_worker, ecs, ancs_per_ec, [], [], value, [], [], [], [],[]).fill_ifa_forms
end

tts.each do |key, value|
  Forms.new(mobile_worker, ecs, ancs_per_ec, [], [], [], value, [], [], [],[]).fill_tt_forms
end

pncs_in_area.each do |key, value|
  Forms.new(mobile_worker, ecs, ancs_per_ec, [], [], [], [], value, [], [],[]).fill_delivery_outcome_forms
end

pncs_out_of_area.each do |key, value|
  Forms.new(mobile_worker, [], [], [], [], [], [], value, [], [],[]).fill_out_of_area_pnc_registration_forms
end

pnc_visits.each do |key, value|
  Forms.new(mobile_worker, ecs, ancs_per_ec, [], [], [], [], pncs, value, [],[]).fill_pnc_visit_forms
end

ppfp_list.each do |key, value|
  Forms.new(mobile_worker, ecs, ancs_per_ec, [], [], [], [], pncs, [], value,[]).fill_ppfp_forms
end

children.each do |key, value|
  Forms.new(mobile_worker, ecs, ancs_per_ec, [], [], [], [], [], [], [], value).fill_child_registration_forms
end
