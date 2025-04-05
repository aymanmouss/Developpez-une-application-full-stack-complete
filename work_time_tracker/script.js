// Fetch the log data from the JSON file
fetch("work_time_log.json")
  .then((response) => response.json())
  .then((data) => {
    console.log(data);
    const weeklyData = processWeeklyData(data.entries);
    console.log(weeklyData);
    displayWeeklyLog(weeklyData);
    calculateWeeklyTotal(weeklyData);
  })
  .catch((error) => console.error("Error fetching log data:", error));

// Function to process data into weekly format
function processWeeklyData(entries) {
  const weeklyData = {};

  entries.forEach((entry) => {
    const date = new Date(entry.time);
    const dateString = date.toISOString().split("T")[0];
    console.log(date.toISOString().split("T"));
    if (!weeklyData[dateString]) {
      weeklyData[dateString] = {
        logs: [],
        totalTime: 0,
      };
    }

    const timeString = date.toTimeString().split(" ")[0].substring(0, 5);
    weeklyData[dateString].logs.push(`${timeString}`);

    if (entry.type === "Check Out" && weeklyData[dateString].logs.length > 1) {
      const checkInTime = new Date(
        entries.find(
          (e) =>
            e.type === "Check In" &&
            new Date(e.time).toISOString().split("T")[0] === dateString
        ).time
      );
      const timeDiff = date - checkInTime;
      weeklyData[dateString].totalTime += timeDiff;
    }
  });

  return weeklyData;
}

// Function to display the weekly log
function displayWeeklyLog(weeklyData) {
  const tableBody = document.querySelector("#weeklyLogTable tbody");
  tableBody.innerHTML = "";

  Object.entries(weeklyData).forEach(([date, data]) => {
    const row = document.createElement("tr");
    const dateCell = document.createElement("td");
    const logsCell = document.createElement("td");
    const totalCell = document.createElement("td");

    dateCell.textContent = date;
    logsCell.textContent = data.logs.join(", ");
    totalCell.textContent = formatTime(data.totalTime);

    row.appendChild(dateCell);
    row.appendChild(logsCell);
    row.appendChild(totalCell);
    tableBody.appendChild(row);
  });
}

// Function to calculate and display the weekly total
function calculateWeeklyTotal(weeklyData) {
  let weeklyTotal = 0;
  Object.values(weeklyData).forEach((data) => {
    weeklyTotal += data.totalTime;
  });

  const weeklyTotalElement = document.getElementById("weeklyTotal");
  weeklyTotalElement.textContent = `Total time for the week: ${formatTime(
    weeklyTotal
  )}`;
}

// Helper function to format time
function formatTime(milliseconds) {
  const hours = Math.floor(milliseconds / (1000 * 60 * 60));
  const minutes = Math.floor((milliseconds % (1000 * 60 * 60)) / (1000 * 60));
  return `${hours}:${minutes.toString().padStart(2, "0")}`;
}
