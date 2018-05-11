// TVDB/TMDB ID
def id = id as int

// database
def db = db

// Sonarr API Configuration
if (db.equals('TheTVDB')) {
    def url = new URL('http://localhost:8989')
    def header = ['X-Api-Key': '']

    def sonarrSeriesId = new JsonSlurper()
        .parseText(new URL(url, '/api/series')
            .get(header)
            .text)
        .find {
            it.tvdbId == id
        }.id

    println new URL(url, '/api/command').post(
        JsonOutput.toJson(
            [name: 'rescanSeries', seriesId: sonarrSeriesId]
        ).getBytes('UTF-8'),
        'application/json',
        header
    ).text
}

// Radarr API Configuration (Currently doesn't work since filebot passes theMovieDB as blank)
if (db.equals('TheMovieDB')) {
    def url = new URL('http://localhost:7878')
    def header = ['X-Api-Key': '']

    def radarrMovieId = new JsonSlurper()
        .parseText(new URL(url, '/api/movie')
            .get(header)
            .text)
        .find {
            it.tmdbId == id
        }.id

    println new URL(url, '/api/command').post(
        JsonOutput.toJson(
            [name: 'rescanMovie', movieId: radarrMovieId]
        ).getBytes('UTF-8'),
        'application/json',
        header
    ).text
}
