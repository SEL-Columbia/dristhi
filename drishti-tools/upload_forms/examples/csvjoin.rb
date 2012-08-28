#!/usr/bin/env ruby

require 'csv'

(STDERR.puts "Usage: csvjoin.rb CSV-FILE-1.csv CSV-FILE-2.csv column-in-file-1=column-in-file2 [column-in-file-1=column-in-file2]"; exit 1) if $ARGV.length < 3
(STDERR.puts "File: #{$ARGV[0]} does not exist."; exit 1) if not File.exists? $ARGV[0]
(STDERR.puts "File: #{$ARGV[1]} does not exist."; exit 1) if not File.exists? $ARGV[1]

first_csv = CSV.read(ARGV[0], {headers: true})
second_csv = CSV.read(ARGV[1], {headers: true})

all_columns_to_match = []
$ARGV[2..-1].each do |column_arg|
  columns = column_arg.split "="
  (STDERR.puts "Column arguments are of type column-in-file-1=column-in-file-2. Check '#{column_arg}'."; exit 1) if columns.size != 2 || columns.any?(&:empty?)
  (STDERR.puts "Column \"#{columns[0]}\" not found in file: #{ARGV[0]}. Columns are: #{first_csv.headers.inspect}"; exit 1) if not first_csv.headers.include? columns[0]
  (STDERR.puts "Column \"#{columns[1]}\" not found in file: #{ARGV[1]}. Columns are: #{second_csv.headers.inspect}"; exit 1) if not second_csv.headers.include? columns[1]
  all_columns_to_match << columns
end

output_csv = CSV.generate do |output|
  output << (first_csv.headers + second_csv.headers)

  first_csv.each do |row_in_first_csv|
    second_csv.each do |row_in_second_csv|
      if all_columns_to_match.all? {|column_to_match| row_in_first_csv[column_to_match[0]] == row_in_second_csv[column_to_match[1]] }
        row_in_second_csv.each {|entry| row_in_first_csv << entry}
        output << row_in_first_csv
      end
    end
  end

end

puts output_csv
