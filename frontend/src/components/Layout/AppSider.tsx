import React from 'react'
import { Layout, Menu } from 'antd'
import {
  HomeOutlined,
  MessageOutlined,
  PictureOutlined,
  VideoCameraOutlined,
  AudioOutlined,
  SoundOutlined,
  SettingOutlined,
  AppstoreOutlined,
  ThunderboltOutlined,
} from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAppStore } from '@stores/useAppStore'
import './AppSider.scss'

const { Sider } = Layout

const menuItems = [
  {
    key: '/',
    icon: <HomeOutlined />,
    label: '首页',
  },
  {
    key: '/chat',
    icon: <MessageOutlined />,
    label: '智能对话',
  },
  {
    key: 'multimodal',
    icon: <AppstoreOutlined />,
    label: '多模态AI',
    children: [
      {
        key: '/multimodal',
        icon: <ThunderboltOutlined />,
        label: '多模态融合',
      },
      {
        key: '/multimodal/image-generation',
        icon: <PictureOutlined />,
        label: '图像生成',
      },
      {
        key: '/multimodal/image-enhancement',
        icon: <PictureOutlined />,
        label: '图像增强',
      },
      {
        key: '/multimodal/video-analysis',
        icon: <VideoCameraOutlined />,
        label: '视频分析',
      },
      {
        key: '/multimodal/audio-analysis',
        icon: <AudioOutlined />,
        label: '音频分析',
      },
      {
        key: '/multimodal/text-to-speech',
        icon: <SoundOutlined />,
        label: '文本转语音',
      },
    ],
  },
  {
    key: '/settings',
    icon: <SettingOutlined />,
    label: '系统设置',
  },
]

const AppSider: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const { collapsed } = useAppStore()

  const handleMenuClick = (key: string) => {
    navigate(key)
  }

  const getSelectedKeys = () => {
    const pathname = location.pathname
    
    // 处理子菜单选中状态
    if (pathname.startsWith('/multimodal/')) {
      return [pathname]
    }
    
    return [pathname]
  }

  const getOpenKeys = () => {
    const pathname = location.pathname
    
    // 如果当前在多模态相关页面，展开多模态菜单
    if (pathname.startsWith('/multimodal/')) {
      return ['multimodal']
    }
    
    return []
  }

  return (
    <Sider
      trigger={null}
      collapsible
      collapsed={collapsed}
      className="app-sider"
      width={240}
    >
      <div className="sider-content">
        <Menu
          mode="inline"
          selectedKeys={getSelectedKeys()}
          defaultOpenKeys={getOpenKeys()}
          items={menuItems}
          onClick={({ key }) => handleMenuClick(key)}
          className="sider-menu"
        />
      </div>
    </Sider>
  )
}

export default AppSider