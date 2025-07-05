#!/usr/bin/env bash
# wait-for-it.sh - espera a que un servicio TCP esté disponible

set -e

host="$1"
port="$2"
shift 2
cmd="$@"

until nc -z "$host" "$port"; do
  echo "Esperando a que $host:$port esté disponible..."
  sleep 2
done

echo "$host:$port está disponible, arrancando comando..."
exec $cmd
