class Row
  def initialize(csv_row)
    @csv_row = csv_row
    @values = {}
  end

  def to_hash
    @values
  end

  def default_value field_header, default_value
    raise "Adding default value: Cannot find field with header: '#{field_header}' in this CSV. Are you using the right CSV?" if not @csv_row.header? field_header
    add_a_field field_header, {:default => default_value}
  end

  def convert_value field_header, conversion_values
    raise "Adding default value: Cannot find field with header: '#{field_header}' in this CSV. Are you using the right CSV?" if not @csv_row.header? field_header
    add_a_field field_header, conversion_values
  end

  def add_field field_header, default_value
    add_a_field field_header, {:empty => default_value}
  end

  def [] header
    raise "Finding value: Cannot find field with header: '#{header}' in this CSV. Are you using the right CSV?" unless @values.key? header
    @values[header]
  end

  private
  def add_a_field field_header, default_values_hash
    @values[field_header] = field(field_header, default_values_hash)
  end

  def field header, default_values_hash
    value_from_row = @csv_row.field(header)

    return default_values_hash[:empty] if value_from_row.nil? or value_from_row.empty?
    return default_values_hash[value_from_row] if default_values_hash.key? value_from_row
    return default_values_hash[:default] if default_values_hash.key? :default
    return value_from_row
  end
end
