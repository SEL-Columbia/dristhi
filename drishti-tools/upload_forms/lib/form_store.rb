class FormStore
  def initialize
    @form_count = 0
  end

  def save_form_instance(form_instance, prefix, suffix)
    @form_count = @form_count + 1
    File.open("output/#{prefix}_#{@form_count.to_s.rjust(4, '0')}_#{suffix}.json", "w") do |f|
      f.puts form_instance
    end
  end
end