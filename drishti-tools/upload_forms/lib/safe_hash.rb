class SafeHash
  def initialize hash
    @map = hash
  end

  def [](key)
    raise "Ouch! Cannot find key: #{key}. The keys are: #{@map.keys.join(', ')}" unless @map.key? key
    @map[key]
  end

  def to_s
    @map
  end
end
