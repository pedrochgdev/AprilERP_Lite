# AprilERP Lite

![Java](https://img.shields.io/badge/Java-21-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## Descripción

AprilERP Lite es un sistema ligero de gestión de inventario y ventas para pequeñas empresas. Con una interfaz intuitiva en Java Swing, ofrece funciones esenciales como control de stock, registro de ventas y seguridad básica, ideal para negocios que inician su digitalización.

---

## Características Principales

- **Gestión de Inventario:** Control de stock en tiempo real y categorización de productos.  
- **Registro de Ventas:** Facturación básica y generación de reportes en PDF.  
- **Seguridad:** Cifrado de contraseñas con `jbcrypt`.  
- **Multi-Base de Datos:** Soporte para Oracle Cloud, MySQL, PostgreSQL y Oracle XE.  

---

## Requisitos

- **JDK 21+**  
- **4 GB RAM** (8 GB recomendado)  
- **500 MB de espacio**  

---

## Instalación

### 1. Clonar el Repositorio
```sh
git clone https://github.com/pedrochgdev/AprilERP_Lite.git
cd AprilERP_Lite
```

### 2. Configurar la Base de Datos
**Para Oracle Cloud Free Tier:**
1. Descarga el Wallet de tu instancia Oracle desde el portal de Oracle Cloud.
2. **Crea la carpeta `wallet` en la raíz del proyecto** y coloca allí los archivos del Wallet.
3. **Edita la clase `BDConnection.java`** (ubicada en `src/database/`) con tus credenciales:
```java
// Configuración para Oracle Cloud
String url = "jdbc:oracle:thin:@aprilerp_high?TNS_ADMIN=wallet";
String user = "TU_USUARIO_ORACLE";  // Reemplazar con tu usuario
String password = "TU_CONTRASEÑA_ORACLE";  // Reemplazar con tu contraseña
```

**Para bases locales (MySQL/PostgreSQL/Oracle XE):**
1. Modifica directamente los parámetros en `BDConnection.java`:
```java
// Ejemplo para MySQL
String url = "jdbc:mysql://localhost:3306/april_erp_lite";
String user = "root";
String password = "tu_contraseña";
```

Ejecuta los scripts SQL de la carpeta `DataBaseSQL/`.

### 3. Agregar Librerías al Classpath
**Librerías requeridas (ubicadas en `lib/oracle/`):**
- `ojdbc17.jar`
- `ucp17.jar`
- `oraclepki.jar`
- `osdt_core.jar`
- `osdt_cert.jar`

**En tu IDE:**
- **Eclipse/NetBeans:** Click derecho en proyecto > Build Path > Add JARs
- **IntelliJ:** File > Project Structure > Libraries > "+" Java
- **CLI:**
  ```sh
  java -cp "AprilERP_Lite.jar:lib/oracle/*" Main
  ```

---

## Uso Rápido
1. **Iniciar sesión:** Usuario: `admin`, Contraseña: `admin123`.  
2. **Agregar producto:** Nombre, precio y stock.  
3. **Vender:** Selecciona productos y genera facturas PDF.  

---

## Tecnologías
- Java Swing  
- MySQL/Oracle/PostgreSQL  
- iTextPDF  
- jBCrypt  

---

## Autor
**Pedro Chávez**  
📧 [gabrielchg6@gmail.com](mailto:gabrielchg6@gmail.com)  
💻 [github.com/pedrochgdev](https://github.com/pedrochgdev)  

---

## Licencia
MIT License. Ver [LICENSE](LICENSE).  

---

## Contribuciones
¡Reporta issues o envía PRs!  
[Issues](https://github.com/pedrochgdev/AprilERP_Lite/issues) | [Pull Requests](https://github.com/pedrochgdev/AprilERP_Lite/pulls)  

---

> 🔄 **Nota:** Una versión mejorada con estructura modular y base de datos avanzada se desarrollará en un repositorio separado.