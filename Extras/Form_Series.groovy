{ import java.math.RoundingMode
  import net.filebot.Language
  def norm = { it.replaceAll(/[`´‘’ʻ""“”]/, "'")
             .replaceAll(/[:|]/, " - ")
             .replaceAll(/[?]/, "!")
             .replaceAll(/[*\s]+/, " ")
             .replaceAll(/\b[IiVvXx]+\b/, { it.upper() })
             .replaceAll(/\b[0-9](?i:th|nd|rd)\b/, { it.lower() }) }
allOf
  // TV Shows directory
  {"TV Shows"}
  // Folder name
  { norm(n).replaceAll(/(S.H.I.E.L.D.)/, " S.H.I.E.L.D ") }
  // Season Folder name
  { episode.special ? 'Specials' : 'Season ' + s.pad(2) }
  { allOf
    // File name
    { norm(n).replaceTrailingBrackets() }
    { episode.special ? 'S00E' + special.pad(2) : S00E00 }
    { allOf
      { norm(t).replacePart(' - Part $1') }
      { allOf
        { allOf
          {"["}
          { allOf
            // Video stream
            { allOf{vf}{vc}.join(" ") }
            { def audioClean = { it.replaceAll(/[\p{Pd}\p{Space}]/, ' ').replaceAll(/\p{Space}{2,}/, ' ') }
              // map Codec + Format Profile
              def mCFP = [ "AC3" : "AC3",
                           "AC3+" : "E-AC3",
                           "AAC LC LC" : "AAC-LC",
                           "AAC LC SBR HE AAC LC": "HE-AAC" ]
              audio.collect { au ->
              def channels = any{ au['ChannelPositions/String2'] }{ au['Channel(s)_Original'] }{ au['Channel(s)'] }
              def ch = channels.replaceAll(/Object\sBased\s\/|0.(?=\d.\d)/, '')
                               .tokenize('\\/').take(3)*.toDouble()
                               .inject(0, { a, b -> a + b }).findAll { it > 0 }
                               .max().toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
              def codec = audioClean(any{ au['CodecID/String'] }{ au['Codec/String'] }{ au['Codec'] })
              def format = any{ au['CodecID/Hint'] }{ au['Format'] }
              def format_profile = { if ( au['Format_Profile'] != null) audioClean(au['Format_Profile']) else '' }
              def combined = allOf{codec}{format_profile}.join(' ')
              def stream = allOf
                             { ch }
                             { mCFP.get(combined, format) }
                             { Language.findLanguage(au['Language']).ISO3.upperInitial() }
              return stream }*.join(" ").join(", ") }
              // logo-free release source finder
            { def websources = readLines("C:/Users/JourneyOver/Dropbox/Public/Folders/Filebot/websources.txt").join("|")
              def lfr = { fn.match(/($websources)\.(?i)WEB/) }
              return allOf{lfr}{"${self.source ?: 'WEB-DL'}"}.join(" ") }
            .join(" - ") }
          {"]"}
          .join("") }
          {subt}
        .join("") }
      .join(" ") }
    .join(" - ") }
  .join("/") }
