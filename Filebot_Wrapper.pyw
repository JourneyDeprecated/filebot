#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import subprocess

# required args
label, state, title, kind, file, directory = sys.argv[1:7]

################################################
### Media file processing script for Filebot ###

# FileBot.
#
# This script is a front-end wrapper for FileBot and the Automated Media Center script.
#
#
# FileBot - http://www.filebot.net
# Forums - https://www.filebot.net/forums
# Format Expressions - http://www.filebot.net/naming.html
# Command Line Interface - http://www.filebot.net/cli.html
#
# Subtitles are provided by OpenSubtitles.org. An OpenSubtitles.org account is required for this feature to
# function. See http://www.opensubtitles.org/en/newuser to register for an account. Once registered, run "filebot
# -script fn:configure" from a command prompt and follow the instructions.
#
#
################################################
### OPTIONS (case sensitive)

### Required ###

# Log File.
# Path to log file.
LOGDIR = '/path/to/logfolder'

# Base destinations to move files, no trailing slash. The subfolder is defined in the format string.
DESTINATION = 'C:/Media'

# Rename action i.e: move | copy | keeplink | symlink | reflink | hardlink | test
ACTION = 'Move'

# Conflict resolution i.e: override | skip | auto | index | fail
CONFLICT = 'Override'

# custom formats (use / instead of \ as directory separator)
movieFormat  = '''{plex}'''
seriesFormat = '''{plex}'''
animeFormat  = '''{plex}'''
musicFormat  = '''{plex}'''

### Optionals ###

# Minimum Video Length (in milliseconds)
MVLEnabled = 'false'
MINLENGTHMS = '300000'

# Minimum File Size (in bytes).
MFSEnabled = 'false'
MINFILESIZE = '50000000'

# Process Music Files
MUSIC = 'false'

# Download subtitles for the given languages i.e: en, de, fr, es, ja, zh, ...
SUBEnabled = 'false'
SUBTITLES = 'en'

# Fetch Artwork/NFO
ARTWORK = 'false'

# Generate *.url files and fetch all available backdrops
EXTRAS  = 'false'

# Tell the given Kodi/XBMN instance to rescan it's library
KODIEnabled = 'false'
KODI = 'host[:port]'

# Tell the given Plex instance to rescan it's library. Plex Home instances require an authentication token
PLEXEnabled = 'false'
PLEX = 'host:apikey'

# Tell the given Emby instance to rescan it's library
EMBYEnabled = 'false'
EMBY = 'host:apikey'

# Save reports to local filesystem
STOREREPORT = 'false'

# Skip extracting file
SKIPEXTRACT = 'false'

# Automatically remove empty folders and clutter files that may be left behind after moving the video files, or temporary extracted files after copying
CLEAN = 'false'

# Delete archives after extraction
DELETEAFTEREXTRACT = 'false'

# Send Rescan Call to Sonarr/radarr (Requires Update_Sonarr_Radarr.groovy script)
# Must go in to groovy script and place API Keys In for both sonarr and radarr
SREnabled = 'false'
SonarrRadarr = '/path/to/Update_Sonarr_Radarr.groovy'

### Media file processing script for Filebot ###
################################################

command = [
  'filebot', '-script', 'fn:amc',
  '--output', DESTINATION,
  '--action', ACTION,
  '--conflict', CONFLICT,
  '-non-strict',
  '--log-file', LOGDIR + '/filebot-amc.log',
  '--def',
    'excludeList=' + LOGDIR + '/filebot-history.log',
    'movieFormat='  + movieFormat,
    'seriesFormat=' + seriesFormat,
    'animeFormat='  + animeFormat,
    'musicFormat='  + musicFormat,
    'ut_label=' + label,
    'ut_state=' + state,
    'ut_title=' + title,
    'ut_kind='  + kind,
    'ut_file='  + file,
    'ut_dir='   + directory
]

### Do Not Edit Beyond This Point ###

if MVLEnabled == "true":
  command += ['minLengthMS=' + MINLENGTHMS]
if MFSEnabled == "true":
  command += ['minFileSize=' + MINFILESIZE]
if MUSIC == "true":
  command += ['music=' + MUSIC]
if SUBEnabled == "true":
  command += ['subtitles=' + SUBTITLES]
if ARTWORK == "true":
  command += ['artwork=' + ARTWORK]
if EXTRAS == "true":
  command += ['extras=' + EXTRAS]
if KODIEnabled == "true":
  command += ['kodi=' + KODI]
if PLEXEnabled == "true":
  command += ['plex=' + PLEX]
if EMBYEnabled == "true":
  command += ['emby=' + EMBY]
if STOREREPORT == "true":
  command += ['storeReport=' + STOREREPORT]
if SKIPEXTRACT == "true":
  command += ['skipExtract=' + SKIPEXTRACT]
if CLEAN == "true":
  command += ['clean=' + CLEAN]
if DELETEAFTEREXTRACT == "true":
  command += ['deleteAfterExtract=' + DELETEAFTEREXTRACT]
if SREnabled == "true":
  command += ['exec=filebot -script ' + SonarrRadarr + ' --def db={info.database} id={id}']

# execute command (and hide cmd window)
subprocess.run(command, creationflags=0x08000000)
