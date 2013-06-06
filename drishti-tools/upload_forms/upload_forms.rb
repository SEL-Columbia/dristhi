require_relative 'lib/dristhi.rb'
require 'fileutils'

def upload_all(filenames, user, password)
  filenames.each_with_index do |filename, index|
    puts "Uploading #{index + 1} of #{filenames.size}: #{filename}"

    Dristhi.new.upload (File.read filename), user, password
    FileUtils.mv filename, 'output/DONE/'
    sleep 1
  end
end

ec_forms = Dir['output/EC_*.json']

upload_all ec_forms, ARGV[0].to_s, ARGV[1].to_s
