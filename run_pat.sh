#!/bin/bash

# Start Spring Boot in the background and capture its PID
./mvnw spring-boot:run -Dspring-boot.run.profiles=secret &
SPRING_PID=$!

# Wait for the app to be ready
echo "Waiting for Spring Boot to start..."
sleep 15

# Run Gatling tests and capture output
echo "====== Running Gatling Tests ======"
./mvnw gatling:test -Pgatling 2>&1 | tee /tmp/gatling_output.txt
GATLING_EXIT=${PIPESTATUS[0]}

# Print the stats.json if available
echo ""
echo "====== Gatling Stats Summary ======"
RESULTS_DIR=$(find target/gatling -maxdepth 1 -mindepth 1 -type d | sort | tail -1)
if [ -n "$RESULTS_DIR" ]; then
    STATS_FILE="$RESULTS_DIR/js/stats.json"
    if [ -f "$STATS_FILE" ]; then
        echo "Results dir: $RESULTS_DIR"
        # Use python to pretty-print the JSON if available
        if command -v python3 &>/dev/null; then
            python3 -m json.tool "$STATS_FILE"
        else
            cat "$STATS_FILE"
        fi
    else
        echo "No stats.json found in $RESULTS_DIR"
    fi
else
    echo "No Gatling results directory found."
fi

# Stop Spring Boot
echo ""
echo "====== Stopping Spring Boot (PID: $SPRING_PID) ======"
kill $SPRING_PID
wait $SPRING_PID 2>/dev/null

exit $GATLING_EXIT
