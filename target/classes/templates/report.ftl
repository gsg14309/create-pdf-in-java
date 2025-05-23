<!DOCTYPE html>
<html>
<head>
    <title>${title}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .header {
            text-align: center;
            margin-bottom: 20px;
            border-bottom: 2px solid #333;
            padding-bottom: 10px;
        }
        .content {
            margin: 20px 0;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        .footer {
            text-align: center;
            margin-top: 20px;
            border-top: 1px solid #ddd;
            padding-top: 10px;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>${title}</h1>
        <p>Generated on: ${.now?string("yyyy-MM-dd HH:mm:ss")}</p>
    </div>
    
    <div class="content">
        <#if items??>
        <table>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Value</th>
            </tr>
            <#list items as item>
            <tr>
                <td>${item.name}</td>
                <td>${item.description}</td>
                <td>${item.value}</td>
            </tr>
            </#list>
        </table>
        </#if>
    </div>
    
    <div class="footer">
        <p>This is a sample report generated using FreeMarker and OpenPDF</p>
    </div>
</body>
</html> 