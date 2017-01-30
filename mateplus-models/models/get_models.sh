#!/bin/bash

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $SCRIPT_DIR

echo "DOWNLOADING ALL FILES"

wget -c 'https://s3.amazonaws.com/repo.iis.memphis.edu/raw-data/srl-EMNLP14%2Bfs-eng.model'
wget -c 'https://s3.amazonaws.com/repo.iis.memphis.edu/raw-data/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model'
wget -c 'https://s3.amazonaws.com/repo.iis.memphis.edu/raw-data/CoNLL2009-ST-English-ALL.anna-3.3.parser.model'
wget -c 'https://s3.amazonaws.com/repo.iis.memphis.edu/raw-data/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model'
wget -c 'https://s3.amazonaws.com/repo.iis.memphis.edu/raw-data/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model'
