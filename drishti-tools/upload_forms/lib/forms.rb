class Forms
  def initialize ec_data, anc_data, anc_services_data
    @ec = ec_data
    @ancs = anc_data
    @anc_services = anc_services_data
  end

  def fill
    fill_ec_form
    fill_anc_registration_forms if has_anc?
    fill_anc_services_forms if has_anc? and has_services?
    fill_anc_outcome_forms if has_anc? and has_services? and has_outcome?
  end

  private
  def fill_ec_form
    puts "EC: #{@ec['Wife Name']} - #{@ec['Husband Name']} - #{@ec['Case ID']}"

    ec_registration_erb = ERB.new(File.read('templates/ec_registration.erb'))

    ec = @ec
    ec_registration_xml = ec_registration_erb.result(binding)
    File.open("output/EC_#{ec['Case ID']}.xml", "w") do |f| f.puts ec_registration_xml end
  end

  def fill_anc_registration_forms
    @ancs.each do |anc|
      puts "    ANC registration: #{anc['LMP']} - #{anc['Case ID']}"

      anc_registration_erb = ERB.new(File.read('templates/anc_registration.erb'))

      ec = @ec
      anc_registration_xml = anc_registration_erb.result(binding)
      File.open("output/ANC_#{anc['Case ID']}.xml", "w") do |f| f.puts anc_registration_xml end
    end
  end

  def fill_anc_services_forms
    @anc_services.each do |anc_service|
      puts "        ANC service: Visit number: #{anc_service['Visit Number']}"

      anc_visit_erb = ERB.new(File.read('templates/anc_visit.erb'))

      anc = @ancs.find {|anc| anc['a.Thayi Card Number'] == anc_service['Thayi Card Number']}
      anc = @ancs.last if anc.nil?
      ec = @ec

      anc_visit_xml = anc_visit_erb.result(binding)
      File.open("output/ANCVisit_#{anc_service['Instance ID']}.xml", "w") do |f| f.puts anc_visit_xml end
    end
  end

  def fill_anc_outcome_forms
    anc_service = @anc_services.first
    puts "    Have ANC outcome: On #{anc_service['Date of Delivery']}. Result: #{anc_service['Outcomes']}"
  end

  def has_anc?
    not @ancs.nil?
  end

  def has_services?
    not @anc_services.nil?
  end

  def has_outcome?
    @anc_services.any? {|service| not service['Date of Delivery'].nil? and not service['Date of Delivery'].empty? }
  end
end

