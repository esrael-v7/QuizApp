const jwt = require('jsonwebtoken');

/**
 * Middleware: Protect routes with JWT authentication
 * Extracts token from Authorization header and verifies it
 */
const protect = (req, res, next) => {
    let token = req.headers.authorization?.split(' ')[1];

    if (!token) {
        return res.status(401).json({ status: 'fail', message: 'Not authorized, no token' });
    }

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.user = decoded;  // Attach user info to request
        next();
    } catch (error) {
        return res.status(401).json({ status: 'fail', message: 'Not authorized, token failed' });
    }
};

/**
 * Middleware: Admin-only route protection
 * Checks if authenticated user has admin role
 */
const adminOnly = (req, res, next) => {
    if (req.user && req.user.role === 'admin') {
        next();
    } else {
        res.status(403).json({ status: 'fail', message: 'Not authorized as an admin' });
    }
};

module.exports = { protect, adminOnly };
