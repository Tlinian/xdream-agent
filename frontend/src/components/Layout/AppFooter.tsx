import React from 'react'
import { Layout } from 'antd'
import './AppFooter.scss'

const { Footer } = Layout

const AppFooter: React.FC = () => {
  const currentYear = new Date().getFullYear()

  return (
    <Footer className="app-footer">
      <div className="footer-content">
        <div className="footer-left">
          <span className="copyright">
            © {currentYear} xdream-agent. All rights reserved.
          </span>
        </div>
        <div className="footer-right">
          <span className="version">
            版本: v1.0.0
          </span>
        </div>
      </div>
    </Footer>
  )
}

export default AppFooter