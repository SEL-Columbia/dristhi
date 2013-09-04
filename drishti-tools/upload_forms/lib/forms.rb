class Forms
  def initialize mobile_worker, ec_data, anc_data, anc_services_data, anc_visits_data, hb_tests_data, ifa_data, tt_data
    @mobile_worker = mobile_worker
    @ec = ec_data
    @ancs = anc_data
    @anc_services = anc_services_data
    @anc_visits = anc_visits_data
    @hb_tests = hb_tests_data
    @ifas = ifa_data
    @tts = tt_data
  end

  def fill_for_in_area
    fill_ec_form
    fill_anc_registration_forms if has_anc?
    #fill_anc_services_forms if has_anc? and has_services?
    #fill_anc_outcome_forms if has_anc? and has_services? and has_outcome?
  end

  def fill_for_out_of_area
    fill_out_of_area_anc_registration_forms if has_anc?
    #fill_anc_services_forms if has_anc? and has_services?
    #fill_anc_outcome_forms if has_anc? and has_services? and has_outcome?
  end

  def fill_ec_form
    puts "EC: #{@ec['Wife Name']} - #{@ec['Husband Name']} - #{@ec['Entity ID']}"

    form_instance_erb = ERB.new(File.read('templates/json_erb/ec_form_instance_erb.json'))
    ec_registration_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

    ec = @ec
    user_id = @mobile_worker.user_id
    user_name = @mobile_worker.user_name
    form_name = "ec_registration"
    instance_id = ec['Instance ID']
    entity_id = ec['Entity ID']

    form_instance = form_instance_erb.result(binding)
    ec_registration_json = ec_registration_erb.result(binding)

    File.open("output/EC_#{ec['Entity ID']}.json", "w") do |f| f.puts ec_registration_json end
  end

  def fill_out_of_area_anc_registration_forms
    @ancs.each do |anc|

      puts "    Out of area ANC registration: #{anc['Wife Name']} - #{anc['Husband Name']} - #{anc['LMP']} - #{anc['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/json_erb/anc_oa_form_instance_erb.json'))
      out_of_area_anc_registration_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      ec = @ec
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "anc_registration_oa"
      instance_id = anc['Instance ID']
      entity_id = anc['Entity ID']

      form_instance = form_instance_erb.result(binding)
      out_of_area_anc_registration_json = out_of_area_anc_registration_erb.result(binding)
      File.open("output/ANCOutOfArea_#{anc['Entity ID']}.json", "w") do |f| f.puts out_of_area_anc_registration_json end
    end
  end

  def fill_anc_registration_forms
    @ancs.each do |value|
      anc = get_safe_map(value)
      puts "    ANC registration: #{anc['LMP']} - #{anc['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/json_erb/anc_form_instance_erb.json'))
      anc_registration_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      ec = @ec
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "anc_registration"
      instance_id = anc['Instance ID']
      entity_id = anc['Entity ID']

      form_instance = form_instance_erb.result(binding)
      anc_registration_json = anc_registration_erb.result(binding)
      File.open("output/ANC_#{anc['Entity ID']}.json", "w") do |f| f.puts anc_registration_json end
    end
  end

  #def fill_anc_services_forms
  #  (@anc_services.sort_by {|service| service['Visit Number']}).each_with_index do |anc_service, index|
  #    puts "        ANC service: Visit number: #{anc_service['Visit Number']}"
  #
  #    anc_visit_erb = ERB.new(File.read('templates/anc_visit.erb'))
  #
  #    anc = @ancs.find {|anc| anc['a.Thayi Card Number'] == anc_service['Thayi Card Number']}
  #    anc = @ancs.last if anc.nil?
  #    ec = @ec
  #    user_id = @mobile_worker.user_id
  #    user_name = @mobile_worker.user_name
  #
  #    anc_visit_xml = anc_visit_erb.result(binding)
  #    File.open("output/ANCVisit_#{index.to_s.rjust(4, '0')}_#{anc_service['Instance ID']}.xml", "w") do |f| f.puts anc_visit_xml end
  #  end
  #end

  #def fill_anc_outcome_forms
  #  anc_outcome_erb = ERB.new(File.read('templates/anc_outcome.erb'))
  #
  #  anc_service = @anc_services.first
  #  anc = @ancs.find {|anc| anc['a.Thayi Card Number'] == anc_service['Thayi Card Number']}
  #  anc = @ancs.last if anc.nil?
  #  user_id = @mobile_worker.user_id
  #  user_name = @mobile_worker.user_name
  #
  #  puts "    Have ANC outcome: On #{anc_service['Date of Delivery']}. Result: #{anc_service['Outcomes']}"
  #
  #  anc_outcome_xml = anc_outcome_erb.result(binding)
  #  File.open("output/ANCOutcome_#{anc_service['Outcome Instance ID']}.xml", "w") do |f| f.puts anc_outcome_xml end
  #end

  def fill_anc_visits_forms
    @anc_visits.each do |anc_visit|
      puts "    ANC Visit: #{anc_visit['Wife Name']} - #{anc_visit['Husband Name']} - #{anc_visit['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/json_erb/anc_visit_form_instance_erb.json'))
      anc_visit_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      ec = get_safe_map(@ec.select { |e|
            e['Village Code'].village.downcase == anc_visit['Village Code'].village.downcase &&
            e['Husband Name'].downcase == anc_visit['Husband Name'].downcase &&
            e['Wife Name'].downcase == anc_visit['Wife Name'].downcase
      })

      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "anc_visit"
      instance_id = anc_visit['Instance ID']
      entity_id = anc_visit['Entity ID']
      submission_date = anc_visit['Submission date']

      form_instance = form_instance_erb.result(binding)
      anc_visit_json = anc_visit_erb.result(binding)
      File.open("output/ANCVisit_#{anc_visit['Entity ID']}.json", "w") do |f| f.puts anc_visit_json end
    end
  end

  def fill_hb_tests_forms
    @hb_tests.each do |hb_test|
      key_for_anc = [hb_test['Village Code'].village.downcase, hb_test['Wife Name'].downcase, hb_test['Husband Name'].downcase]
      puts "    Hb Test: #{hb_test['Wife Name']} - #{hb_test['Husband Name']} - #{hb_test['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/json_erb/hb_test_form_instance_erb.json'))
      hb_test_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      anc_detail_for_given_couple = @ancs.select { |k, v|
        k == key_for_anc
      }

      anc = get_safe_map(anc_detail_for_given_couple[key_for_anc])
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "hb_test"
      instance_id = hb_test['Instance ID']
      entity_id = hb_test['Entity ID']
      submission_date = hb_test['Submission date']

      form_instance = form_instance_erb.result(binding)
      hb_test_json = hb_test_erb.result(binding)

      File.open("output/HbTest_#{hb_test['Entity ID']}.json", "w") do |f| f.puts hb_test_json end
    end
    end

  def fill_ifa_forms
    @ifas.each do |ifa|
      key_for_anc = [ifa['Village Code'].village.downcase, ifa['Wife Name'].downcase, ifa['Husband Name'].downcase]
      puts "    IFA : #{ifa['Wife Name']} - #{ifa['Husband Name']} - #{ifa['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/json_erb/ifa_form_instance_erb.json'))
      ifa_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      anc_detail_for_given_couple = @ancs.select { |k, v|
        k == key_for_anc
      }

      anc = get_safe_map(anc_detail_for_given_couple[key_for_anc])
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "ifa"
      instance_id = ifa['Instance ID']
      entity_id = ifa['Entity ID']
      submission_date = ifa['Submission date']

      form_instance = form_instance_erb.result(binding)
      ifa_json = ifa_erb.result(binding)

      File.open("output/IFA_#{ifa['Entity ID']}.json", "w") do |f| f.puts ifa_json end
    end
  end

  def fill_tt_forms
    @tts.each do |tt|
      key_for_anc = [tt['Village Code'].village.downcase, tt['Wife Name'].downcase, tt['Husband Name'].downcase]
      puts "    TT : #{tt['Wife Name']} - #{tt['Husband Name']} - #{tt['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/json_erb/tt_form_instance_erb.json'))
      ifa_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      anc_detail_for_given_couple = @ancs.select { |k, v|
        k == key_for_anc
      }

      anc = get_safe_map(anc_detail_for_given_couple[key_for_anc])
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      dosage = tt['TT dose'].gsub('tt', '') # 1, 2 or booster
      form_name = "tt_#{dosage}"
      bind_path = "/model/instance/TT#{dosage.capitalize}_EngKan/"
      instance_id = tt['Instance ID']
      entity_id = tt['Entity ID']
      submission_date = tt['Submission date']

      form_instance = form_instance_erb.result(binding)
      ifa_json = ifa_erb.result(binding)

      File.open("output/TT#{dosage.capitalize}_#{tt['Entity ID']}.json", "w") do |f| f.puts ifa_json end
    end
  end

  def has_anc?
    not (@ancs.nil? or @ancs.to_a.empty?)
  end

  def has_services?
    not (@anc_services.nil? or @anc_services.to_a.empty?)
  end

  def has_outcome?
    @anc_services.any? {|service| not service['Date of Delivery'].nil? and not service['Date of Delivery'].empty? }
  end

  def has_anc_visits?
    not (@anc_visits.nil? or @anc_visits.to_a.empty?)
  end

  def has_hb_tests?
    not (@hb_tests.nil? or @hb_tests.to_a.empty?)
  end

  def has_ifas?
    not (@ifas.nil? or @ifas.to_a.empty?)
  end

  def has_tts?
    not (@tts.nil? or @tts.to_a.empty?)
  end

  private
  def get_safe_map(value)
    raise "Multiple values found for key : [#{value[0]['Village Code'].village}, #{value[0]['Wife Name']}, #{value[0]['Husband Name']}]" if value.size > 1
    value[0]
  end

end

