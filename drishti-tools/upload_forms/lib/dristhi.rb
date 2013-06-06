require 'net/http/post/multipart'

class Dristhi
  def upload(content, user, password)
    file_contents = StringIO.new content
    file_contents = file_contents.string
    url = URI.parse('http://drishti.modilabs.org/form-submissions')
    req = Net::HTTP::Post.new(url.path, initheader = {'Content-Type' =>'application/json'})
    req.basic_auth user, password
    req.body = file_contents
    http = Net::HTTP.new(url.host, url.port)

    response = http.start { |http| http.request(req) }

    puts response.code
  end
end
