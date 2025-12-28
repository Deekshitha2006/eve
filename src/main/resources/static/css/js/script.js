// --- EVENT ALERT SYSTEM (DEBUGGED & FIXED) --- //

function scanEvents() {
    // 1. Get all hidden date inputs and badges
    const dateInputs = document.querySelectorAll('.event-iso-date');
    const badges = document.querySelectorAll('.time-badge');

    let urgentCount = 0;
    let warningCount = 0;
    let errorCount = 0; // For debugging

    const now = new Date();

    console.log("Scan Started..."); // DEBUGGING LINE 1
    console.log("Inputs found:", dateInputs.length); // DEBUGGING LINE 2

    // 2. Loop through events
    dateInputs.forEach((input, index) => {
        if (!index < badges.length) return;

        const isoDate = input.value; // Read from Hidden Input (Format: 2023-10-10)
        const eventTime = new Date(isoDate); // Standard parsing
        const diffMs = eventTime - now;
        const diffMins = Math.floor(diffMs / 60000);

        console.log(`Event ${index + 1}: Date=${isoDate}, Diff=${diffMins}`); // DEBUGGING LINE 3

        const badge = badges[index];

        if (diffMins < 30) {
            badge.innerText = "CRITICAL (<30m)";
            badge.className = "time-badge time-urgent";
            urgentCount++;
        } else if (diffMins < 120) {
            badge.innerText = "URGENT (<2h)";
            badge.className = "time-badge time-warning";
            warningCount++;
        } else if (diffMins < 0) {
            badge.innerText = "EXPIRED";
            badge.className = "time-badge time-expired";
            errorCount++;
        } else {
            badge.innerText = "OK";
            badge.className = "time-badge"; // Green
        }
    });

    // 3. Show Result (Alert)
    let message = "> SYSTEM SCANNED DATABASE...\n";
    message += `> Critical Events: ${urgentCount}\n`;
    message += `> Urgent Events: ${warningCount}\n`;

    if (errorCount > 0) {
        message += `> Warning: ${errorCount} events are in the past!`;
    }

    alert(message);
}

// 4. Booking Confirmation
function confirmBooking(eventName) {
    const msg = `System: Confirm booking for "${eventName}"?\n\n> User Profile: ID [1]\n> Status: Ready.`;
    if (confirm(msg)) {
        return true; // Proceeds to link
    }
    return false; // Stops link
}