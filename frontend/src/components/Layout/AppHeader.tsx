import React, { useState } from 'react'
import { Layout, Avatar, Dropdown, Button, Space, Badge, Tooltip } from 'antd'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  SettingOutlined,
  BellOutlined,
  LogoutOutlined,
  GithubOutlined,
  QuestionCircleOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useAppStore } from '@stores/useAppStore'
import './AppHeader.scss'

const { Header } = Layout

const AppHeader: React.FC = () => {
  const navigate = useNavigate()
  const { collapsed, toggleCollapsed } = useAppStore()
  const [notificationCount] = useState(3)

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人资料',
      onClick: () => navigate('/settings/profile'),
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '系统设置',
      onClick: () => navigate('/settings'),
    },
    {
      type: 'divider' as const,
      key: 'divider1',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: () => {
        // TODO: 实现退出登录逻辑
        console.log('退出登录')
      },
    },
  ]

  const helpMenuItems = [
    {
      key: 'docs',
      icon: <QuestionCircleOutlined />,
      label: '使用文档',
      onClick: () => window.open('https://github.com/xdream-agent/docs', '_blank'),
    },
    {
      key: 'github',
      icon: <GithubOutlined />,
      label: 'GitHub',
      onClick: () => window.open('https://github.com/xdream-agent', '_blank'),
    },
  ]

  return (
    <Header className="app-header">
      <div className="header-left">
        <Button
          type="text"
          icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          onClick={toggleCollapsed}
          className="trigger"
        />
        <div className="logo">
          <span className="logo-text">xdream-agent</span>
        </div>
      </div>

      <div className="header-right">
        <Space size="middle">
          <Tooltip title="通知">
            <Badge count={notificationCount} size="small">
              <Button
                type="text"
                icon={<BellOutlined />}
                className="header-icon"
                onClick={() => navigate('/notifications')}
              />
            </Badge>
          </Tooltip>

          <Dropdown
            menu={{ items: helpMenuItems }}
            placement="bottomRight"
            arrow
          >
            <Tooltip title="帮助">
              <Button
                type="text"
                icon={<QuestionCircleOutlined />}
                className="header-icon"
              />
            </Tooltip>
          </Dropdown>

          <Dropdown
            menu={{ items: userMenuItems }}
            placement="bottomRight"
            arrow
          >
            <div className="user-info">
              <Avatar size="small" icon={<UserOutlined />} />
              <span className="user-name">管理员</span>
            </div>
          </Dropdown>
        </Space>
      </div>
    </Header>
  )
}

export default AppHeader