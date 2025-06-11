<#--
  Advanced FreeMarker Template Example
  ------------------------------------
  This template demonstrates:
    - Macros (reusable components)
    - Error handling with <#attempt>/<#recover>
    - Conditional rendering and null checks
    - Dynamic tables and charts
    - CSS styling
    - Built-in functions and formatting
    - Use of data model fields
-->

<#-- Macro: header - Renders the report header with title, subtitle, and metadata -->
<#macro header title subtitle="">
    <div class="header">
        <h1>${title}</h1>
        <#if subtitle?has_content>
            <h2>${subtitle}</h2>
        </#if>
        <div class="meta">
            <span class="timestamp">Generated: ${.now?string("yyyy-MM-dd HH:mm:ss")}</span>
            <#if report.metadata??>
                <span class="version">v${report.metadata.version!}</span>
            </#if>
        </div>
    </div>
</#macro>

<#-- Macro: statusBadge - Renders a colored badge based on status value -->
<#macro statusBadge status>
    <#switch status>
        <#case "COMPLETED">
            <span class="badge success">✓ Completed</span>
            <#break>
        <#case "PROCESSING">
            <span class="badge warning">⟳ Processing</span>
            <#break>
        <#case "FAILED">
            <span class="badge error">✗ Failed</span>
            <#break>
        <#default>
            <span class="badge">${status}</span>
    </#switch>
</#macro>

<#-- Macro: dataTable - Renders a table for a list of items, with error handling -->
<#macro dataTable items title="Data Table">
    <div class="table-container">
        <h3>${title}</h3>
        <#attempt>
            <#if items?? && items?size gt 0>
                <table class="data-table">
                    <thead>
                        <tr>
                            <#-- Dynamically render table headers from first item's keys -->
                            <#list items[0]?keys as key>
                                <th>${key?cap_first}</th>
                            </#list>
                        </tr>
                    </thead>
                    <tbody>
                        <#list items as item>
                            <tr class="${item.highlight?then('highlight', '')}">
                                <#list item?keys as key>
                                    <td>
                                        <#-- Render status badge or format value, else print value -->
                                        <#if key == "status">
                                            <@statusBadge item[key]/>
                                        <#elseif key == "value">
                                            ${item[key]?string("0.00")}
                                        <#else>
                                            ${item[key]!}
                                        </#if>
                                    </td>
                                </#list>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            <#else>
                <p class="no-data">No data available</p>
            </#if>
        <#recover>
            <p class="error">Error rendering table: ${.error}</p>
        </#attempt>
    </div>
</#macro>

<#-- Macro: chart - Renders a simple bar chart from a list of data -->
<#macro chart data title="Chart">
    <div class="chart-container">
        <h3>${title}</h3>
        <#attempt>
            <#if data??>
                <div class="chart">
                    <#list data as item>
                        <div class="bar" style="width: ${item.value}%">
                            <span class="label">${item.name}</span>
                            <span class="value">${item.value?string("0.00")}</span>
                        </div>
                    </#list>
                </div>
            <#else>
                <p class="no-data">No chart data available</p>
            </#if>
        <#recover>
            <p class="error">Error rendering chart: ${.error}</p>
        </#attempt>
    </div>
</#macro>

<!DOCTYPE html>
<html>
<head>
    <title>${report.title}</title>
    <style>
        :root {
            --primary-color: #2c3e50;
            --success-color: #27ae60;
            --warning-color: #f39c12;
            --error-color: #e74c3c;
            --background-color: #f8f9fa;
            --border-color: #dee2e6;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: var(--primary-color);
            background-color: var(--background-color);
            margin: 0;
            padding: 20px;
        }

        .header {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }

        .meta {
            display: flex;
            gap: 20px;
            color: #666;
            font-size: 0.9em;
            margin-top: 10px;
        }

        .badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.9em;
            font-weight: 500;
        }

        .badge.success { background-color: var(--success-color); color: white; }
        .badge.warning { background-color: var(--warning-color); color: white; }
        .badge.error { background-color: var(--error-color); color: white; }

        .table-container {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .data-table th,
        .data-table td {
            padding: 12px;
            border: 1px solid var(--border-color);
            text-align: left;
        }

        .data-table th {
            background-color: #f8f9fa;
            font-weight: 600;
        }

        .data-table tr.highlight {
            background-color: #fff3cd;
        }

        .chart-container {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }

        .chart {
            display: flex;
            flex-direction: column;
            gap: 10px;
            margin-top: 10px;
        }

        .bar {
            background-color: var(--primary-color);
            color: white;
            padding: 8px;
            border-radius: 4px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .error {
            color: var(--error-color);
            padding: 10px;
            background-color: #fde8e8;
            border-radius: 4px;
        }

        .no-data {
            color: #666;
            font-style: italic;
        }

        .footer {
            text-align: center;
            margin-top: 40px;
            padding: 20px;
            color: #666;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <#-- Render the header macro with title and subtitle -->
    <@header title=report.title subtitle=report.subtitle!""/>

    <div class="content">
        <#-- Use <#attempt> to catch errors in the main content block -->
        <#attempt>
            <#-- Conditionally render summary if present -->
            <#if report.summary??>
                <div class="table-container">
                    <h3>Summary</h3>
                    <table class="data-table">
                        <tr>
                            <td>Status</td>
                            <td><@statusBadge report.status/></td>
                        </tr>
                        <tr>
                            <td>Total Items</td>
                            <td>${report.items?size}</td>
                        </tr>
                        <#if report.statistics??>
                            <tr>
                                <td>Average Value</td>
                                <td>${report.statistics.averageValue?string("0.00")}</td>
                            </tr>
                            <tr>
                                <td>Total Value</td>
                                <td>${report.statistics.totalValue?string("0.00")}</td>
                            </tr>
                        </#if>
                    </table>
                </div>
            </#if>

            <#-- Render the dataTable macro for the items list -->
            <@dataTable items=report.items title="Items"/>

            <#-- Conditionally render the chart macro if chartData is present -->
            <#if report.chartData??>
                <@chart data=report.chartData title="Value Distribution"/>
            </#if>

        <#recover>
            <div class="error">
                <h3>Error Processing Report</h3>
                <p>${.error}</p>
            </div>
        </#attempt>
    </div>

    <div class="footer">
        <p>Generated by Advanced FreeMarker Report</p>
        <#-- Show environment info if present -->
        <#if report.metadata??>
            <p>Environment: ${report.metadata.environment!}</p>
        </#if>
    </div>
</body>
</html> 