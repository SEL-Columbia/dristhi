class RandomCommCareID
  def initialize
    @valid = {}
    @valid[:digits] = (0..9).to_a.collect {|num| num.to_s}
    @valid[:alphanumeric] = ('A'..'Z').to_a + @valid[:digits]

    @rand = Random.new
  end

  def next(length, type_of_random_string)
    random_num = ""
    valid_values_for_this_type = @valid[type_of_random_string]

    length.times.each {|x| random_num += valid_values_for_this_type[Random.new.rand(valid_values_for_this_type.size)] }
    random_num
  end
end
