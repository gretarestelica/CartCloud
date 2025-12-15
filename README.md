# CartCloud

## Si të nisësh lokalisht

### Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

Variablat e dobishme të mjedisit (opsionale, për databazën):

```bash
SPRING_DATASOURCE_URL=jdbc:sqlserver://GR\\MSSQLSERVER01:1433;databaseName=CartCloudDB;encrypt=false;trustServerCertificate=true;
SPRING_DATASOURCE_USERNAME=cartcloud_user
SPRING_DATASOURCE_PASSWORD=YOUR_PASSWORD
```

### Frontend (Vite + React)

```bash
cd frontend/frontend
npm install
npm run dev
```

Opsionale: përcakto URL-në e API-së:

```bash
set VITE_API_BASE=http://localhost:8080/api
```