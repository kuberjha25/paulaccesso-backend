-- init.sql - Complete initialization script
SET FOREIGN_KEY_CHECKS = 0;

-- Create tables if not exists
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    emp_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    location_state VARCHAR(100),
    designation VARCHAR(255),
    department VARCHAR(100),
    company VARCHAR(50),
    role ENUM('ADMIN', 'RECEPTIONIST', 'EMPLOYEE') DEFAULT 'EMPLOYEE',
    photo LONGTEXT,
    is_active BOOLEAN DEFAULT TRUE,
    mobile VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_emp_id (emp_id)
);

CREATE TABLE IF NOT EXISTS visitors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    mobile VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    company VARCHAR(255),
    address TEXT,
    person_to_meet VARCHAR(255) NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    photo LONGTEXT,
    checkout_photo LONGTEXT,
    id_proof LONGTEXT,
    tag_number VARCHAR(50),
    check_in_time DATETIME NOT NULL,
    check_out_time DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    meeting_status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'COMPLETED') DEFAULT 'PENDING',
    user_id BIGINT,
    person_to_meet_emp_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_person_to_meet (person_to_meet),
    INDEX idx_tag_number (tag_number),
    INDEX idx_user_id (user_id),
    CONSTRAINT fk_visitor_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS tag_numbers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_number VARCHAR(50) NOT NULL UNIQUE,
    is_available BOOLEAN DEFAULT TRUE,
    assigned_to_visitor_id BIGINT,
    assigned_at DATETIME,
    released_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tag_number (tag_number),
    INDEX idx_is_available (is_available),
    CONSTRAINT fk_tag_visitor FOREIGN KEY (assigned_to_visitor_id) REFERENCES visitors(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS otps (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    otp VARCHAR(10) NOT NULL,
    expires_at DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email_otp (email, otp),
    INDEX idx_expires_at (expires_at)
);

-- Insert admin user if not exists
INSERT IGNORE INTO users (emp_id, name, email, location_state, designation, department, company, role, is_active, created_at) 
VALUES ('ADMIN001', 'Kuber Jha', 'kuber98jha@gmail.com', 'CHANDIGARH', 'ADMIN', 'IT', 'PMFPL', 'ADMIN', 1, NOW());

INSERT IGNORE INTO users (emp_id, name, email, location_state, designation, department, company, role, is_active, created_at) 
VALUES ('TEST001', 'Tester', 'test@pml.com', 'CHANDIGARH', 'Tester', 'IT', 'PMFPL', 'ADMIN', 1, NOW());

-- Insert 50 tags if not exists
INSERT IGNORE INTO tag_numbers (tag_number, is_available, created_at) VALUES
('1', 1, NOW()), ('2', 1, NOW()), ('3', 1, NOW()), ('4', 1, NOW()), ('5', 1, NOW()),
('6', 1, NOW()), ('7', 1, NOW()), ('8', 1, NOW()), ('9', 1, NOW()), ('10', 1, NOW()),
('11', 1, NOW()), ('12', 1, NOW()), ('13', 1, NOW()), ('14', 1, NOW()), ('15', 1, NOW()),
('16', 1, NOW()), ('17', 1, NOW()), ('18', 1, NOW()), ('19', 1, NOW()), ('20', 1, NOW()),
('21', 1, NOW()), ('22', 1, NOW()), ('23', 1, NOW()), ('24', 1, NOW()), ('25', 1, NOW()),
('26', 1, NOW()), ('27', 1, NOW()), ('28', 1, NOW()), ('29', 1, NOW()), ('30', 1, NOW()),
('31', 1, NOW()), ('32', 1, NOW()), ('33', 1, NOW()), ('34', 1, NOW()), ('35', 1, NOW()),
('36', 1, NOW()), ('37', 1, NOW()), ('38', 1, NOW()), ('39', 1, NOW()), ('40', 1, NOW()),
('41', 1, NOW()), ('42', 1, NOW()), ('43', 1, NOW()), ('44', 1, NOW()), ('45', 1, NOW()),
('46', 1, NOW()), ('47', 1, NOW()), ('48', 1, NOW()), ('49', 1, NOW()), ('50', 1, NOW());

SET FOREIGN_KEY_CHECKS = 1;

SELECT CONCAT('Database initialized at ', NOW()) as Status;