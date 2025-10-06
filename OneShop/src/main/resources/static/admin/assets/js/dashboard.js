/**
 * Initialize ApexCharts for the dashboard
 */
function initDashboardCharts() {
    // Revenue Chart
    const revenueChartEl = document.getElementById('revenueChart');
    if (revenueChartEl && typeof ApexCharts !== 'undefined') {
        const revenueChart = new ApexCharts(revenueChartEl, {
            chart: {
                height: 300,
                type: 'line',
                zoom: { enabled: false },
                toolbar: { show: false },
                fontFamily: 'Public Sans, sans-serif',
                animations: {
                    enabled: true,
                    easing: 'easeinout',
                    speed: 800,
                    animateGradually: {
                        enabled: true,
                        delay: 150
                    },
                    dynamicAnimation: {
                        enabled: true,
                        speed: 350
                    }
                }
            },
            colors: ['#7367F0'],
            dataLabels: { enabled: false },
            stroke: {
                curve: 'smooth',
                width: 3,
                lineCap: 'round'
            },
            series: [{
                name: 'Doanh thu',
                data: [15000000, 18000000, 21000000, 19000000, 23000000, 25000000, 28000000]
            }],
            xaxis: {
                categories: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7'],
                axisBorder: { show: false },
                axisTicks: { show: false },
                tooltip: { enabled: false },
                labels: {
                    style: {
                        fontFamily: 'Public Sans, sans-serif',
                        colors: '#6c757d'
                    }
                }
            },
            yaxis: {
                labels: {
                    formatter: function(value) {
                        return (value / 1000000) + 'M';
                    },
                    style: {
                        fontFamily: 'Public Sans, sans-serif',
                        colors: '#6c757d'
                    }
                }
            },
            tooltip: {
                y: {
                    formatter: function(value) {
                        return value.toLocaleString('vi-VN') + ' VNĐ';
                    }
                },
                style: {
                    fontFamily: 'Public Sans, sans-serif'
                }
            },
            grid: {
                borderColor: '#e5e9f2',
                strokeDashArray: 4,
                padding: {
                    top: 0,
                    right: 0,
                    bottom: 0,
                    left: 0
                },
                xaxis: {
                    lines: {
                        show: false
                    }
                },
                yaxis: {
                    lines: {
                        show: true
                    }
                }
            },
            fill: {
                type: 'gradient',
                gradient: {
                    shade: 'light',
                    type: 'vertical',
                    shadeIntensity: 0.4,
                    gradientToColors: ['#7367F0'],
                    inverseColors: false,
                    opacityFrom: 0.4,
                    opacityTo: 0.1,
                    stops: [0, 100]
                }
            },
            markers: {
                size: 5,
                colors: ['#7367F0'],
                strokeColors: '#fff',
                strokeWidth: 2,
                hover: {
                    size: 6
                }
            }
        });
        
        // Render the chart
        revenueChart.render();

        // Update chart on window resize
        window.addEventListener('resize', function() {
            revenueChart.updateOptions({
                chart: {
                    height: 300
                }
            });
        });
    }
}

/**
 * Initialize dashboard components
 */
function initDashboard() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize charts
    initDashboardCharts();

    // Add animation class to cards on scroll
    const animateOnScroll = function() {
        const cards = document.querySelectorAll('.dashboard-card');
        cards.forEach(card => {
            const cardTop = card.getBoundingClientRect().top;
            const windowHeight = window.innerHeight;
            if (cardTop < windowHeight - 100) {
                card.classList.add('animate__animated', 'animate__fadeInUp');
            }
        });
    };

    // Initial check
    animateOnScroll();
    
    // Check on scroll
    window.addEventListener('scroll', animateOnScroll);
}

// Initialize dashboard when DOM is fully loaded
document.addEventListener('DOMContentLoaded', initDashboard);
