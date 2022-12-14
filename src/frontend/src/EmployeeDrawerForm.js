import {Drawer, Input, Col, Select, Form, Row, Button, Spin} from 'antd';
import { addNewEmployee } from "./client";
import {LoadingOutlined} from "@ant-design/icons";
import {useState} from "react";
import {successNotification, errorNotification} from "./Notification";

const {Option} = Select;

const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;

function EmployeeDrawerForm({showDrawer, setShowDrawer, fetchEmployees}) {
    const onClose = () => setShowDrawer(false);
    const [submitting, setSubmitting] = useState(false);

    const onFinish = employee => {
        setSubmitting(true)
        console.log(JSON.stringify(employee, null, 2))
        addNewEmployee(employee)
            .then(()=> {
                console.log("employee added")
                onClose()
                successNotification("Employee successfully added", `${employee.name} was added to the system`)
                fetchEmployees();
            }).catch(err => {
            console.log(err);
            err.response.json().then(res => {
                console.log(res);
                errorNotification(
                    "There was an issue",
                    `${res.message} [${res.status}] [${res.error}]`,
                    "bottomLeft"
                )
            });
            }).finally(() => {
                setSubmitting(false);
            })
    };

    const onFinishFailed = errorInfo => {
        alert(JSON.stringify(errorInfo, null, 2));
    };

    return <Drawer
        title="Create new employee"
        width={720}
        onClose={onClose}
        visible={showDrawer}
        bodyStyle={{paddingBottom: 80}}
        footer={
            <div
                style={{
                    textAlign: 'right',
                }}
            >
                <Button onClick={onClose} style={{marginRight: 8}}>
                    Cancel
                </Button>
            </div>
        }
    >
        <Form layout="vertical"
              onFinishFailed={onFinishFailed}
              onFinish={onFinish}
              hideRequiredMark>
            <Row gutter={16}>
                <Col span={12}>
                    <Form.Item
                        name="name"
                        label="Name"
                        rules={[{required: true, message: 'Please enter employee name'}]}
                    >
                        <Input placeholder="Please enter employee name"/>
                    </Form.Item>
                </Col>
                <Col span={12}>
                    <Form.Item
                        name="email"
                        label="Email"
                        rules={[{required: true, message: 'Please enter employee email'}]}
                    >
                        <Input placeholder="Please enter employee email"/>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={16}>
                <Col span={12}>
                    <Form.Item
                        name="gender"
                        label="gender"
                        rules={[{required: true, message: 'Please select a gender'}]}
                    >
                        <Select placeholder="Please select a gender">
                            <Option value="MALE">MALE</Option>
                            <Option value="FEMALE">FEMALE</Option>
                            <Option value="OTHER">OTHER</Option>
                        </Select>
                    </Form.Item>
                </Col>
            </Row>
            <Row>
                <Col span={12}>
                    <Form.Item >
                        <Button type="primary" htmlType="submit">
                            Submit
                        </Button>
                    </Form.Item>
                </Col>
            </Row>
            <Row>
                {submitting && <Spin indicator={antIcon} />}
            </Row>
        </Form>
    </Drawer>
}

export default EmployeeDrawerForm;