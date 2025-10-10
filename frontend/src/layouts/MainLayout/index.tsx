import React, { useState } from 'react'
import { Layout, Menu, Avatar, Dropdown, Button, Space, theme } from 'antd'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { UserOutlined, HomeOutlined, MessageOutlined, SettingOutlined, AppstoreOutlined, PictureOutlined, VideoCameraOutlined, AudioOutlined, ThunderboltOutlined, SoundOutlined, MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons'
import type { MenuProps } from 'antd'
import './index.scss'

const { Header, Content, Sider } = Layout

const MainLayout: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const [collapsed, setCollapsed] = useState(false)

  const {
    token: { colorBgContainer },
  } = theme.useToken()

  // 处理子菜单展开状态
  const [openKeys, setOpenKeys] = useState<string[]>([])

  // 处理子菜单展开/收起
  const handleOpenChange = (keys: string[]) => {
    setOpenKeys(keys)
  }

  // 菜单项
  const menuItems: MenuProps['items'] = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页',
      onClick: () => navigate('/')
    },
    {
      key: '/chat',
      icon: <MessageOutlined />,
      label: 'AI对话',
      onClick: () => navigate('/chat')
    },
    {
      key: 'multimodal-menu',
      icon: <AppstoreOutlined />,
      label: '多模态服务',
      children: [
        {
          key: '/multimodal',
          icon: <ThunderboltOutlined />,
          label: '服务概览',
          onClick: () => navigate('/multimodal')
        },
        {
          key: '/multimodal/fusion',
          icon: <ThunderboltOutlined />,
          label: '多模态内容融合',
          onClick: () => navigate('/multimodal/fusion')
        },
        {
          key: '/multimodal/image-generation',
          icon: <PictureOutlined />,
          label: 'AI图像生成',
          onClick: () => navigate('/multimodal/image-generation')
        },
        {
          key: '/multimodal/image-enhancement',
          icon: <PictureOutlined />,
          label: '图像增强处理',
          onClick: () => navigate('/multimodal/image-enhancement')
        },
        {
          key: '/multimodal/video-generation',
          icon: <VideoCameraOutlined />,
          label: 'AI视频生成',
          onClick: () => navigate('/multimodal/video-generation')
        },
        {
          key: '/multimodal/video-analysis',
          icon: <VideoCameraOutlined />,
          label: '视频内容分析',
          onClick: () => navigate('/multimodal/video-analysis')
        },
        {
          key: '/multimodal/audio-analysis',
          icon: <AudioOutlined />,
          label: '音频智能分析',
          onClick: () => navigate('/multimodal/audio-analysis')
        },
        {
          key: '/multimodal/text-to-speech',
          icon: <SoundOutlined />,
          label: '文本转语音',
          onClick: () => navigate('/multimodal/text-to-speech')
        }
      ]
    },
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: '设置',
      onClick: () => navigate('/settings')
    }
  ]

  // 用户菜单
  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      label: '个人资料',
      onClick: () => navigate('/profile')
    },
    {
      key: 'settings',
      label: '设置',
      onClick: () => navigate('/settings')
    },
    {
      type: 'divider'
    },
    {
      key: 'logout',
      label: '退出登录',
      onClick: () => {
        // 处理退出登录
        console.log('退出登录')
      }
    }
  ]

  // 获取当前选中的菜单项
  const getSelectedKeys = () => {
    const path = location.pathname
    if (path.startsWith('/multimodal/')) {
      return [path]
    }
    return [path]
  }

  // 获取当前展开的子菜单
  const getOpenKeys = () => {
    const path = location.pathname
    if (path.startsWith('/multimodal/')) {
      return ['multimodal-menu']
    }
    return openKeys
  }

  return (
    <Layout className="main-layout" style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        theme="light"
        className="main-sider"
      >
        <div className="logo">
          <img src="/logo.svg" alt="Logo" />
          {!collapsed && (
            <div className="logo-content">
              <div className="logo-title">Xdream Agent</div>
              <div className="logo-subtitle">向梦而生，追逐成梦之秘</div>
            </div>
          )}
        </div>
        <Button
          type="text"
          icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          onClick={() => setCollapsed(!collapsed)}
          className="collapse-button"
          style={{
            position: 'absolute',
            top: '20px',
            right: '-15px',
            width: '30px',
            height: '30px',
            borderRadius: '50%',
            backgroundColor: '#fff',
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            zIndex: 10
          }}
        />
        <Menu
          theme="light"
          mode="inline"
          selectedKeys={getSelectedKeys()}
          openKeys={getOpenKeys()}
          onOpenChange={handleOpenChange}
          items={menuItems}
          className="main-menu"
        />
      </Sider>
      
      <Layout>
        <Header className="main-header" style={{ background: colorBgContainer }}>
          <div className="header-left">
            {/* 可以添加面包屑或其他头部内容 */}
          </div>
          <div className="header-right">
            <Space size="middle">
              <Button type="text" icon={<SettingOutlined />} onClick={() => navigate('/settings')} />
              <Dropdown menu={{ items: userMenuItems }} placement="bottomRight" arrow>
                <Avatar icon={<UserOutlined />} className="user-avatar" />
              </Dropdown>
            </Space>
          </div>
        </Header>
        
        <Content className="main-content">
          <div className="content-wrapper">
            <Outlet />
          </div>
        </Content>
        
        <footer className="main-footer">
          <div className="footer-content">
            <p>&copy; 2024 Xdream Agent. 向梦而生，追逐成梦之秘</p>
          </div>
        </footer>
      </Layout>
    </Layout>
  )
}

export default MainLayout