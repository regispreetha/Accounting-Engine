# Reconciliation Engine - Frontend

## Overview

AngularJS-based web interface for the Reconciliation Engine.

## Technology Stack

- **Framework**: AngularJS 1.8.3
- **UI Library**: Bootstrap 5.3.0
- **Icons**: Bootstrap Icons
- **Build**: npm

## Project Structure

```
frontend/
├── index.html                          # Main HTML file
├── package.json                        # npm dependencies
├── js/
│   ├── app.js                         # Main AngularJS module
│   ├── services/
│   │   └── apiService.js              # Backend API integration
│   └── controllers/
│       ├── dashboardController.js     # Dashboard logic
│       ├── rulesController.js         # Rules builder logic
│       ├── reconciliationController.js # Reconciliation execution
│       ├── runsController.js          # Runs listing and details
│       └── exceptionsController.js    # Exception management
├── views/
│   ├── dashboard.html                 # Dashboard view
│   ├── rules.html                     # Rules builder view
│   ├── reconciliation.html            # Execution view
│   ├── runs.html                      # Runs listing view
│   ├── run-details.html               # Run details view
│   └── exceptions.html                # Exception management view
└── css/
    └── app.css                        # Custom styles
```

## Setup and Installation

### Prerequisites

- Node.js and npm
- Running backend API (default: http://localhost:8080)

### Installation

```bash
npm install
```

### Run Development Server

```bash
npm start
```

The application will be available at: `http://localhost:8081`

## Configuration

### API Endpoint

Edit `js/app.js` to change the backend API URL:

```javascript
app.constant('API_CONFIG', {
    baseUrl: 'http://localhost:8080/api'
});
```

## Features

### 1. Dashboard

**Route**: `#!/dashboard`

- Real-time statistics
- Recent reconciliation runs
- Open exceptions summary
- Quick navigation

### 2. Rules Builder

**Route**: `#!/rules`

Three-panel interface:

**Rule Groups Panel**
- Create and manage rule groups
- View all rule groups
- Select rule group for editing

**Rules Panel**
- Create reconciliation rules
- Configure rule types and matching types
- Set tolerance values
- Delete rules

**Matching Criteria Panel**
- Add matching criteria to rules
- Define field comparisons
- Set logical operators (AND/OR)
- Delete criteria

### 3. Execute Reconciliation

**Route**: `#!/reconciliation`

- Select rule group
- Define reconciliation period
- Enter run name
- Execute reconciliation
- View execution results

### 4. Reconciliation Runs

**Route**: `#!/runs`

- List all reconciliation runs
- Search and filter runs
- View run status
- Navigate to run details

### 5. Run Details

**Route**: `#!/runs/:runId`

- Run summary and statistics
- Matched transactions
- Exception details
- Tabbed interface for easy navigation

### 6. Exception Management

**Route**: `#!/exceptions`

- View all exceptions
- Filter by status (Open, In Progress, Resolved)
- Update exception status
- Add resolution comments
- Severity-based highlighting

## AngularJS Components

### Controllers

#### DashboardController
- Loads dashboard statistics
- Displays recent runs
- Shows open exceptions

#### RulesController
- Manages rule groups, rules, and criteria
- Three-panel CRUD interface
- Cascading selection

#### ReconciliationController
- Loads active rule groups
- Executes reconciliation
- Displays execution results

#### RunsController / RunDetailsController
- Lists all runs
- Shows detailed run information
- Displays matches and exceptions

#### ExceptionsController
- Manages exception workflow
- Status updates
- Resolution tracking

### Services

#### ApiService
- HTTP calls to backend API
- Centralized API integration
- Error handling

### Routing

Configured in `app.js` using `ngRoute`:

```javascript
$routeProvider
    .when('/dashboard', {...})
    .when('/rules', {...})
    .when('/reconciliation', {...})
    .when('/runs', {...})
    .when('/runs/:runId', {...})
    .when('/exceptions', {...})
    .otherwise({redirectTo: '/dashboard'});
```

## UI Components

### Navigation Bar
- Fixed top navigation
- Links to all major sections
- Responsive design

### Cards
- Styled containers for content
- Headers with actions
- Consistent spacing

### Tables
- Hover effects
- Responsive design
- Status badges
- Action buttons

### Forms
- Bootstrap form controls
- Validation
- Clear labels
- Submit/Cancel buttons

### Modals
- Exception resolution
- Confirmation dialogs
- Bootstrap modal component

## Styling

### Custom CSS (app.css)

- Dashboard card animations
- Table hover effects
- Badge styling
- Form enhancements
- Responsive design
- Modal customizations

### Bootstrap Integration

- Grid system for layout
- Form controls
- Buttons and badges
- Tables
- Navigation
- Modals

### Icons

Bootstrap Icons used throughout:
- Navigation icons
- Action buttons
- Status indicators
- Dashboard widgets

## Best Practices

### Code Organization

1. **Separation of Concerns**: Controllers handle logic, views handle presentation
2. **Reusable Services**: API calls centralized in ApiService
3. **Consistent Naming**: Follow AngularJS conventions
4. **Error Handling**: Centralized error handler

### Performance

1. **One-time Binding**: Use `::` for static data
2. **Track By**: Use `track by` in ng-repeat
3. **Lazy Loading**: Load data on demand
4. **Minimize Watchers**: Avoid complex expressions in templates

### User Experience

1. **Loading Indicators**: Show spinner during API calls
2. **Confirmation Dialogs**: Confirm destructive actions
3. **Success Messages**: Alert on successful operations
4. **Error Messages**: Clear error feedback
5. **Responsive Design**: Mobile-friendly interface

## Testing

### Manual Testing Checklist

- [ ] Dashboard loads with statistics
- [ ] Can create rule groups
- [ ] Can create rules and criteria
- [ ] Can execute reconciliation
- [ ] Can view run details
- [ ] Can manage exceptions
- [ ] Navigation works correctly
- [ ] Forms validate properly
- [ ] Error handling works
- [ ] Responsive on mobile

### Browser Compatibility

Tested on:
- Chrome (recommended)
- Firefox
- Safari
- Edge

## Development

### Adding New Features

1. **Create View**: Add HTML file in `views/`
2. **Create Controller**: Add controller in `js/controllers/`
3. **Add Route**: Update `app.js` with new route
4. **Add API Methods**: Update `apiService.js` if needed
5. **Update Navigation**: Add link in `index.html`

### Code Style

- Use 4-space indentation
- Follow AngularJS style guide
- Comment complex logic
- Use meaningful variable names

## Deployment

### Production Build

For production, consider:

1. **Minification**: Minify JavaScript and CSS
2. **CDN**: Use CDN for libraries
3. **Caching**: Configure proper cache headers
4. **HTTPS**: Always use HTTPS in production

### Nginx Configuration

Example nginx.conf:

```nginx
server {
    listen 80;
    server_name reconciliation.example.com;
    root /var/www/reconciliation-engine/frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## Troubleshooting

### Common Issues

1. **API Connection Error**
   - Check backend is running
   - Verify API_CONFIG.baseUrl
   - Check CORS configuration

2. **Views Not Loading**
   - Check route configuration
   - Verify view file paths
   - Check browser console for errors

3. **Data Not Displaying**
   - Check API responses in network tab
   - Verify controller bindings
   - Check for JavaScript errors

## Contributing

1. Follow AngularJS best practices
2. Test on multiple browsers
3. Update documentation
4. Maintain consistent code style

## License

Copyright (c) 2025 Reconciliation Engine Team
