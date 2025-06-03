window.addEventListener("DOMContentLoaded", function () {
  // Dummy data for dashboard
  document.getElementById("totalRevenue").textContent = "25,000,000 đ";
  document.getElementById("totalOrders").textContent = "320";
  document.getElementById("totalUsers").textContent = "152";
  document.getElementById("totalProducts").textContent = "89";

  const ctx = document.getElementById('salesChart').getContext('2d');
  new Chart(ctx, {
    type: 'line',
    data: {
      labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      datasets: [{
        label: 'Weekly Sales (đ)',
        data: [3000000, 4000000, 3500000, 5000000, 6000000, 7000000, 6500000],
        borderColor: '#0d6efd',
        backgroundColor: 'rgba(13, 110, 253, 0.1)',
        fill: true,
        tension: 0.3
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: function(value) {
              return value.toLocaleString('vi-VN') + ' đ';
            }
          }
        }
      }
    }
  });
});
