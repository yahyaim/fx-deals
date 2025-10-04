#!/usr/bin/env bash
# wait-for-it.sh - wait until a TCP host:port is available
# Usage: ./wait-for-it.sh host:port -- command args

set -e

hostport=$1
shift

host=$(echo $hostport | cut -d: -f1)
port=$(echo $hostport | cut -d: -f2)

until nc -z "$host" "$port"; do
  echo "Waiting for $host:$port..."
  sleep 1
done

exec "$@"
