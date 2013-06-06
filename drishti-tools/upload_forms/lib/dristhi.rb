require 'net/http'

class Dristhi
  def upload(content, user, password)
    url = URI.parse('https://drishti.modilabs.org/form-submissions')
    req = Net::HTTP::Post.new(url.path, initheader = {'Content-Type' =>'application/json'})
    req.basic_auth user, password
    req.body = content
    http = Net::HTTP.new(url.host, url.port)
    http.use_ssl = true
    http.ca_file = '../../assets/security/certificates/drishti.server.crt'

    response = http.start { |http| http.request(req) }

    puts response.code
  end
end
