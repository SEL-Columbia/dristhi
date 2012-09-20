require_relative 'lib/read_anc_services_from_xlsx.rb'
require_relative 'lib/read_ancs_from_xlsx.rb'
require_relative 'lib/read_ecs_from_xlsx.rb'
require_relative 'lib/forms.rb'

Dir['output/*.xml'].each {|file| FileUtils.rm_f file}

ecs = ECs.new("examples/Munjanhalli.xlsx").ecs
ancs_per_ec = ANCs.new("examples/Munjanhalli.xlsx").ancs_grouped_per_couple
anc_services_per_ec = ANCServices.new("examples/Munjanhalli.xlsx").anc_services_per_ec

puts "Got: ECs: #{ecs.size}. ANCS grouped by EC: #{ancs_per_ec.size}. Services grouped by EC: #{anc_services_per_ec.size}"

# In area ANCs.
ecs.each do |ec|
  key = [ec['Village Code'].village, ec['Wife Name'], ec['Husband Name']]

  Forms.new(ec, ancs_per_ec[key], anc_services_per_ec[key]).fill_for_in_area
end

# Out of area ANCs.
ancs_per_ec.each do |anc_key, anc_values|
  next if ecs.any? {|ec| anc_key == [ec['Village Code'].village, ec['Wife Name'], ec['Husband Name']]}

  Forms.new(nil, anc_values, anc_services_per_ec[anc_key]).fill_for_out_of_area
end
