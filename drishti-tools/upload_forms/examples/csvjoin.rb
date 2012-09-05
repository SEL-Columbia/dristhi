#!/usr/bin/env ruby

require 'csv'

error_message = %q(Usage: csvjoin.rb [PREFIX-FOR-CSV1=]CSV-FILE-1.csv [PREFIX-FOR-CSV2=]CSV-FILE-2.csv column-in-file-1=column-in-file2 [column-in-file-1=column-in-file2]

For example: csvjoin.rb EC_=ec_details.csv anc_details.csv number=ec_number village=village

This will give back a CSV with rows where the "number" column in ec_details.csv is the same as "ec_number" column in
anc_details.csv AND the "village" column in both CSV files is the same.

Also, in this case, the headers of the ec_details.csv will be prefixed with "EC_" (optional prefix has been provided).
Something like this:

EC_number,EC_village,EC_othercolumn,ec_number,village,othercolumn
1,"Village 1","Some other value",1,"Village 1","Some value in ANC"
2,"Village 2","Some value",2,"Village 2","Some value")

(STDERR.puts error_message; exit 1) if $ARGV.length < 3

(prefix_for_first_csv, filename_for_first_csv) = ($ARGV[0] =~ /=/ ? $ARGV[0] : "=" + $ARGV[0]).split(/=/)
(prefix_for_second_csv, filename_for_second_csv) = ($ARGV[1] =~ /=/ ? $ARGV[1] : "=" + $ARGV[1]).split(/=/)

(STDERR.puts "File: #{filename_for_first_csv} does not exist."; exit 1) if not File.exists? filename_for_first_csv
(STDERR.puts "File: #{filename_for_second_csv} does not exist."; exit 1) if not File.exists? filename_for_second_csv

first_csv = CSV.read(filename_for_first_csv, {headers: true})
second_csv = CSV.read(filename_for_second_csv, {headers: true})

all_columns_to_match = []
$ARGV[2..-1].each do |column_arg|
  columns = column_arg.split "="
  (STDERR.puts "Column arguments are of type column-in-file-1=column-in-file-2. Check '#{column_arg}'."; exit 1) if columns.size != 2 || columns.any?(&:empty?)
  (STDERR.puts "Column \"#{columns[0]}\" not found in file: #{ARGV[0]}. Columns are: #{first_csv.headers.inspect}"; exit 1) if not first_csv.headers.include? columns[0]
  (STDERR.puts "Column \"#{columns[1]}\" not found in file: #{ARGV[1]}. Columns are: #{second_csv.headers.inspect}"; exit 1) if not second_csv.headers.include? columns[1]
  all_columns_to_match << columns
end

output_csv = CSV.generate do |output|
  output << (first_csv.headers.collect {|header| prefix_for_first_csv + header} + second_csv.headers.collect {|header| prefix_for_second_csv + header})

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
