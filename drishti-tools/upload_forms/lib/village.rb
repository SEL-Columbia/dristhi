class Village

  def self.code_to_village_hash
    {
        "29230030066" => Village.new("bherya", "bherya_a", "ardha_bherya"),
        "29230060072" => Village.new("bherya", "bherya_a", "basavanapura"),
        "29230030063" => Village.new("bherya", "bherya_a", "geradada"),
        "29230030067" => Village.new("bherya", "bherya_a", "sambaravalli"),
        "29230030064" => Village.new("bherya", "bherya_a", "somanahalli_colony"),
        "29230030065" => Village.new("bherya", "bherya_b", "battiganahalli"),
        "29230030069" => Village.new("bherya", "bherya_b", "sugganahalli"),
        "29230030066a" => Village.new("bherya", "bherya_b", "half_bherya"),
        "29230030059" => Village.new("bherya", "g_a_guppe", "arakere"),
        "29230030056" => Village.new("bherya", "g_a_guppe", "g_a_guppe"),
        "29230030056a" => Village.new("bherya", "g_a_guppe", "udayagiri"),
        "29230030056b" => Village.new("bherya", "hosa_agrahara", "boregowdanakoppalu"),
        "29230030071" => Village.new("bherya", "hosa_agrahara", "harambanahalli"),
        "29230030071a" => Village.new("bherya", "hosa_agrahara", "harambanahalli_koppalu"),
        "29230030070" => Village.new("bherya", "hosa_agrahara", "hosa_agrahara"),
        "29230030068" => Village.new("bherya", "hosa_agrahara", "mandiganahalli"),
        "29230030061" => Village.new("bherya", "munjanahalli", "chikkabherya"),
        "29230030058" => Village.new("bherya", "munjanahalli", "kavalu_hosur"),
        "29230030060" => Village.new("bherya", "munjanahalli", "munjanahalli"),
        "29230040138" => Village.new("keelanapura", "keelanapura_a", "inam_uttanahalli"),
        "29230040113" => Village.new("keelanapura", "keelanapura_a", "keelanapura"),
        "29230040113a" => Village.new("keelanapura", "keelanapura_a", "m_c_hundi"),
        "29230040137" => Village.new("keelanapura", "keelanapura_b", "hosahalli"),
        "29230040137a" => Village.new("keelanapura", "keelanapura_b", "gurukarapura"),
        "29230040112" => Village.new("keelanapura", "keelanapura_b", "madhavagere"),
        "29230040114" => Village.new("keelanapura", "keelanapura_b", "megalapura"),
        "29230040111" => Village.new("keelanapura", "puttegowdanahundi", "chattanahalli"),
        "29230040111a" => Village.new("keelanapura", "puttegowdanahundi", "chattanahallipalya"),
        "29230040116" => Village.new("keelanapura", "puttegowdanahundi", "duddagere"),
        "29230040039" => Village.new("keelanapura", "puttegowdanahundi", "lakshmipura"),
        "29230040039a" => Village.new("keelanapura", "puttegowdanahundi", "mahadevi_colony"),
        "29230040110" => Village.new("keelanapura", "puttegowdanahundi", "puttegowdanahundi"),
        "29230040104" => Village.new("keelanapura", "vajamangala", "chikkahalli"),
        "29230040104a" => Village.new("keelanapura", "vajamangala", "halagayanahundi"),
        "29230040101" => Village.new("keelanapura", "vajamangala", "vajamangala"),
        :empty => Village.new("bherya", "munjanahalli", "munjanahalli"),
        :default => Village.new("bherya", "munjanahalli", "munjanahalli")
    }
  end


  attr_reader :phc, :sc, :village

  def initialize phc, sc, village
    @phc = phc
    @sc = sc
    @village = village
  end

end
