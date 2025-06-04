window.addEventListener("DOMContentLoaded", function () {

  const ctx = document.getElementById('salesChart').getContext('2d');

  new Chart(ctx, {
    type: 'line',
    data: {
      labels: chartData.map(item => item.label),
      datasets: [{
        label: 'Sales ($)',
        data: chartData.map(item => item.value),
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
              return value.toLocaleString('en-US') + ' $';
            }
          }
        }
      },
      plugins: {
        tooltip: {
          callbacks: {
            label: function(context) {
              return context.dataset.label + ': ' + context.raw.toLocaleString('en-US') + ' $';
            }
          }
        }
      }
    }
  });
});
